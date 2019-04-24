package com.shopee.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author kong
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface RouterParam {
    String value() default "";
}