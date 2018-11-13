package com.kong.router;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author kong
 * 完整uri跳转路径
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface RouterUri {
    String value() default "";
}
