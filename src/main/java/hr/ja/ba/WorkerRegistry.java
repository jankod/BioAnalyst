package hr.ja.ba;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerRegistry {

    private final ApplicationContext applicationContext;

    @Getter
    private final Map<String, WorkerDescriptor> registry = new LinkedHashMap<>();

    @PostConstruct
    public void scanAndRegister() {
        Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(BioWorker.class);

        annotatedBeans.forEach((beanName, bean) -> {
            if (!(bean instanceof Worker worker)) {
                log.warn("Bean {} je oznacen s @BioWorker ali ne implementira Worker", beanName);
                return;
            }
            BioWorker meta = bean.getClass().getAnnotation(BioWorker.class);
            WorkerDescriptor descriptor = buildDescriptor(worker, meta);
            WorkerDescriptor previous = registry.putIfAbsent(descriptor.name(), descriptor);
            if (previous != null) {
                log.warn("Preskacem duplikat workera {} ({}) jer je vec registriran ({})",
                        descriptor.name(), bean.getClass().getName(), previous.implementationClass().getName());
            } else {
                log.info("Registriran worker: {} ({})", descriptor.name(), bean.getClass().getSimpleName());
            }
        });

        log.info("WorkerRegistry ucitan, broj registriranih workera: {}", registry.size());
    }

    public Collection<WorkerDescriptor> findAll() {
        return registry.values();
    }

    public Optional<WorkerDescriptor> findByName(String name) {
        return Optional.ofNullable(registry.get(name));
    }

    private WorkerDescriptor buildDescriptor(Worker worker, BioWorker meta) {
        Class<? extends WorkerInput> inputType = resolveInputType(worker);
        boolean hasUi = inputType != null;
        return new WorkerDescriptor(
                meta.name(),
                meta.description(),
                meta.version(),
                worker.getClass(),
                hasUi,
                inputType,
                worker
        );
    }

    private Class<? extends WorkerInput> resolveInputType(Worker worker) {
        if (!(worker instanceof WorkerUI)) {
            return null;
        }
        try {
            Method createInput = worker.getClass().getMethod("createInput");
            Class<?> returnType = createInput.getReturnType();
            if (WorkerInput.class.isAssignableFrom(returnType)) {
                @SuppressWarnings("unchecked")
                Class<? extends WorkerInput> casted = (Class<? extends WorkerInput>) returnType;
                return casted;
            }
        } catch (NoSuchMethodException e) {
            log.warn("WorkerUI {} nema createInput metodu", worker.getClass().getName(), e);
        }
        return null;
    }

    public record WorkerDescriptor(
            String name,
            String description,
            String version,
            Class<?> implementationClass,
            boolean uiCapable,
            Class<? extends WorkerInput> inputType,
            Worker worker
    ) {
    }
}
