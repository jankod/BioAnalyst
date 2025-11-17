package hr.ja.w2.core;

import hr.ja.w2.annotation.BioWorker;
import hr.ja.w2.annotation.Run;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WorkerRegistry implements ApplicationContextAware {

    private final Map<String, WorkerDefinition> definitions = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        scanWorkers();
    }

    private void scanWorkers() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(BioWorker.class);
        beans.forEach((beanName, bean) -> {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            BioWorker annotation = targetClass.getAnnotation(BioWorker.class);
            Method runMethod = findRunMethod(targetClass);
            Run run = runMethod.getAnnotation(Run.class);
            String resolvedName = resolveWorkerName(annotation, beanName);

            WorkerDefinition definition = new WorkerDefinition(
                    resolvedName,
                    annotation.description(),
                    bean,
                    runMethod,
                    run
            );
            definitions.put(resolvedName, definition);
            log.info("Registered W2 worker '{}' running method {}#{}", resolvedName, targetClass.getSimpleName(), runMethod.getName());
        });
    }

    private Method findRunMethod(Class<?> targetClass) {
        Method found = null;
        for (Method method : targetClass.getMethods()) {
            if (method.isAnnotationPresent(Run.class)) {
                if (found != null) {
                    throw new IllegalStateException("Multiple @Run methods found in " + targetClass.getName());
                }
                found = method;
            }
        }
        if (found == null) {
            throw new IllegalStateException("No @Run method found in " + targetClass.getName());
        }
        return found;
    }

    private String resolveWorkerName(BioWorker annotation, String beanName) {
        if (StringUtils.hasText(annotation.name())) {
            return annotation.name();
        }
        if (StringUtils.hasText(annotation.value())) {
            return annotation.value();
        }
        return beanName;
    }

    public Optional<WorkerDefinition> find(String workerName) {
        return Optional.ofNullable(definitions.get(workerName));
    }

    public Collection<WorkerDefinition> all() {
        return definitions.values();
    }
}
