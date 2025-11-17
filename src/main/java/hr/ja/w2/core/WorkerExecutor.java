package hr.ja.w2.core;

import hr.ja.w2.annotation.Run;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerExecutor {

    private final WorkerRegistry registry;

    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final AsyncTaskExecutor taskExecutor;

    private final Map<String, WorkerRunInstance> activeRuns = new ConcurrentHashMap<>();
    private final List<WorkerRunInstance> runHistory = new CopyOnWriteArrayList<>();

    public WorkerRunInstance start(String workerName, Map<String, String> params) {
        WorkerDefinition definition = registry.find(workerName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown worker: " + workerName));
        Map<String, String> safeParams = params == null ? Map.of() : new LinkedHashMap<>(params);
        validateRequiredParams(definition, safeParams);

        String runId = UUID.randomUUID().toString();
        WorkerRunInstance runInstance = new WorkerRunInstance(runId, definition, safeParams);

        Future<?> future = taskExecutor.submit(() -> execute(definition, runInstance));
        runInstance.attachFuture(future);
        activeRuns.put(runId, runInstance);
        log.info("Started W2 worker {} run {}", workerName, runId);
        return runInstance;
    }

    private void validateRequiredParams(WorkerDefinition definition, Map<String, String> params) {
        Run runAnnotation = definition.runAnnotation();
        if (runAnnotation == null) {
            return;
        }
        for (String required : runAnnotation.requiredParams()) {
            if (!StringUtils.hasText(required)) {
                continue;
            }
            if (!params.containsKey(required)) {
                throw new IllegalArgumentException("Missing required param '" + required + "' for worker " + definition.name());
            }
        }
    }

    private void execute(WorkerDefinition definition, WorkerRunInstance runInstance) {
        runInstance.markRunning();
        Instant startedAt = runInstance.getStartedAt();
        try {
            WorkerContext context = new WorkerContext(runInstance.getId(), runInstance.getParams());
            Map<String, String> output = invoke(definition, context);
            WorkerRunResult result = WorkerRunResult.success(output, startedAt, Instant.now());
            runInstance.markCompleted(result);
        } catch (Exception ex) {
            log.error("Worker {} run {} failed", definition.name(), runInstance.getId(), ex);
            WorkerRunResult result = WorkerRunResult.failure(ex.getMessage(), startedAt, Instant.now());
            runInstance.markFailed(result);
        } finally {
            activeRuns.remove(runInstance.getId());
            runHistory.add(runInstance);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> invoke(WorkerDefinition definition, WorkerContext context) throws Exception {
        Method method = definition.runMethod();
        Object bean = definition.bean();
        Object returnValue;
        switch (method.getParameterCount()) {
            case 0 -> returnValue = method.invoke(bean);
            case 1 -> {
                Class<?> paramType = method.getParameterTypes()[0];
                if (WorkerContext.class.isAssignableFrom(paramType)) {
                    returnValue = method.invoke(bean, context);
                } else if (Map.class.isAssignableFrom(paramType)) {
                    returnValue = method.invoke(bean, context.params());
                } else {
                    throw new IllegalStateException("@Run method parameter must be WorkerContext or Map<String,String>");
                }
            }
            default -> throw new IllegalStateException("@Run method cannot declare more than one parameter");
        }
        return normalizeOutput(returnValue);
    }

    private Map<String, String> normalizeOutput(Object rawResult) {
        if (rawResult == null) {
            return Map.of();
        }
        if (rawResult instanceof Map<?, ?> map) {
            Map<String, String> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> copy.put(String.valueOf(key), String.valueOf(value)));
            return Collections.unmodifiableMap(copy);
        }
        throw new IllegalStateException("@Run method must return Map<String,String> or void");
    }

    public Collection<WorkerDefinition> definitions() {
        return registry.all();
    }

    public List<WorkerRunInstance> activeRuns() {
        return new ArrayList<>(activeRuns.values());
    }

    public List<WorkerRunInstance> history() {
        return List.copyOf(runHistory);
    }

    public WorkerRunInstance requireRun(String runId) {
        WorkerRunInstance instance = activeRuns.get(runId);
        if (instance != null) {
            return instance;
        }
        return runHistory
                .stream()
                .filter(run -> run.getId().equals(runId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No run with id " + runId));
    }

    public boolean stop(String runId) {
        WorkerRunInstance instance = activeRuns.get(runId);
        if (instance == null) {
            return false;
        }
        Future<?> future = instance.getFuture();
        boolean cancelled = future != null && future.cancel(true);
        if (cancelled) {
            instance.markCancelled();
            activeRuns.remove(runId);
            runHistory.add(instance);
        }
        return cancelled;
    }
}
