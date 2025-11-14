package hr.ja.ba;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class WorkerManager {

    private final Map<String, WorkerHolder> workers = new ConcurrentHashMap<>();
    private final List<WorkerHolder> workersHistory = new CopyOnWriteArrayList<>();

    private final Map<String, AbstractWorker> availableWorkers;

    @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private final AsyncTaskExecutor taskExecutor;

    @ShellMethod(key = "start", value = "Start a specific worker in background")
    public String start(@ShellOption String workerName) {
        AbstractWorker worker = availableWorkers.get(workerName);
        if (worker == null) {
            return "Unknown worker: " + workerName;
        }

        WorkerHolder existing = workers.get(getWorkerName(workerName));
        if (existing != null && existing.isActive()) {
            return "Worker " + workerName + " already running.";
        }

        worker.reset();
        workers.remove(workerName);
        WorkerHolder holder = new WorkerHolder(workerName, worker, Instant.now());
        Future<?> future = taskExecutor.submit(() -> {
            try {
                worker.run();
            } finally {
                holder.markFinished();
                addToHistoryIfMissing(holder);
                log.info("Worker {} finished.", workerName);
            }
        });
        holder.attachFuture(future);
        workers.put(workerName, holder);
        log.info("Started worker: {}", workerName);
        return "Started worker: " + workerName;
    }

    private String lastWorkerName = null;

    private String getWorkerName(String workerName) {
        if (workerName == null) {
            if (lastWorkerName == null) {
                throw new IllegalArgumentException("Worker name must be provided at least once.");
            }
            return lastWorkerName;
        }
        lastWorkerName = workerName;
        return workerName;
    }

    // show history of workers
    @ShellMethod(key = "listh", value = "Show history of all workers")
    public String history() {
        StringBuilder sb = new StringBuilder();
        for (WorkerHolder holder : workersHistory) {
            sb.append("Worker: ").append(holder.getName()).append("\n");
            sb.append("Started: ").append(MyUtil.format( holder.getStartedAt())).append("\n");
            sb.append("Finished: ").append(holder.getFinishedAt()).append("\n");
            sb.append("Status: ").append(holder.getWorker().getStatus().getProgressInfo()).append("\n");
            sb.append("Result: ").append(holder.getWorker().getStatus().getResult()).append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    @ShellMethod(key = "stop", value = "Stop a specific worker")
    public String stop(@ShellOption(defaultValue = ShellOption.NULL) String workerName) {
        workerName = getWorkerName(workerName);
        WorkerHolder holder = workers.get(workerName);
        if (holder == null || holder.getFuture() == null) {
            return "Worker " + workerName + " nije pokrenut.";
        }

        holder.getWorker().cancel();
        //    - **Preporuka**: Dodati timeout s vanjskim threadom koji će prisilno prekinuti
        boolean interrupted = holder.getFuture().cancel(true );
        if (interrupted) {
            holder.markFinished();
            addToHistoryIfMissing(holder);
        }
        log.info("Stopped worker: {}", workerName);
        return interrupted ? "Stopped worker: " + workerName : "It is not possible to stop worker: " + workerName;
    }

    @ShellMethod(key = "status", value = "Show status of a specific worker")
    public String status(@ShellOption(defaultValue = ShellOption.NULL) String workerName) {
        workerName = getWorkerName(workerName);
        WorkerHolder holder = workers.get(workerName);
        if (holder == null) {
            return "Not started.";
        }
        WorkerStatus status = holder.getWorker().getStatus();
        return status.toString();
    }

    @ShellMethod(key = "info", value = "Details about a specific worker")
    public String info(@ShellOption(defaultValue = ShellOption.NULL) String workerName) {
        workerName = getWorkerName(workerName);
        WorkerHolder holder = workers.get(workerName);
        if (holder == null) {
            return "Not started.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Worker: ").append(workerName).append("\n");
        sb.append("Active: ").append(holder.isActive()).append("\n");
        sb.append("Started: ").append(holder.getStartedAt()).append("\n");
        sb.append("Finished: ").append(holder.getFinishedAt()).append("\n");
        sb.append("Status: ").append(holder.getWorker().getStatus().getProgressInfo()).append("\n");
        Future<?> future = holder.getFuture();
        if (future != null && future.isDone()) {
            sb.append("Future state: done\n");
        } else if (future != null) {
            sb.append("Future state: running\n");
        }
        return sb.toString();
    }

    @ShellMethod(key = "list", value = "Show all available workers and their status")
    public String list() {
        StringBuilder sb = new StringBuilder();
        for (String workerName : availableWorkers.keySet()) {
            sb.append(workerName).append(": ").append(status(workerName)).append("\n");
        }
        return sb.toString();
    }

    private void addToHistoryIfMissing(WorkerHolder holder) {
        if (!workersHistory.contains(holder)) {
            workersHistory.add(holder);
        }
    }
}
