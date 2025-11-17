package hr.ja.w2.core;

import hr.ja.w2.annotation.Run;

import java.lang.reflect.Method;

public record WorkerDefinition(
        String name,
        String description,
        Object bean,
        Method runMethod,
        Run runAnnotation
) {

    public WorkerDefinition {
        runMethod.setAccessible(true);
    }
}
