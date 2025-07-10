package com.hhy.retryerboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 方法重试注解
 * </p>
 *
 * @Author hhy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryer {
    /**
     * 重试次数，默认: 3次
     */
    int maxAttempts() default 3;

    /**
     * 重试间隔时长，默认: 2
     */
    int time() default 2;

    /**
     * 时间单位，默认: 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 匹配返回值
     */
    String[] result() default {};

    /**
     * 返回值的类型
     */
    Class[] resultClass() default {};

    /**
     * 匹配抛出的异常，以全限定名存储
     */
    Class<? extends Throwable>[] exception() default {};

    /**
     * 监听器名称，默认: 无
     */
    String listenerName() default "";

    /**
     * 自定义线程池的BeanName
     */
    String taskExecutorName() default "";
}
