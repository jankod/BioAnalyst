package hr.ja.w2.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a Spring bean as W2 worker definition.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component
public @interface BioWorker {

    /**
     * Unique worker name that users reference when starting work.
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * Human friendly worker name. Falls back to {@link #value()} when empty.
     */
    String name() default "";

    String description() default "";
}
