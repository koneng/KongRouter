package com.shopee.router.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @author kong
 * 用于标识目标Activity path
 */
@Documented
@Target(TYPE)
@Retention(CLASS)
public @interface RouterTarget {
    String path() default "";
}
