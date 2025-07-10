package com.hhy.retryer.core;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @Author hhy
 */
@FunctionalInterface
public interface RetryerSupplier<T> {
    T exec();
}
