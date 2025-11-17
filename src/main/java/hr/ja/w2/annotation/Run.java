package hr.ja.w2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks the method the framework invokes for this worker.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Run {

    /**
     * Optional list of parameters that must be present before the worker starts.
     */
    String[] requiredParams() default {};
}
