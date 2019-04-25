package com.shopee.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author kong
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface RouterField {
    String value() default "";
}