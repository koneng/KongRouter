package com.kong.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author kong
 * uri 的host和path部分路径
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface RouterPath {
    String value() default "";
}
