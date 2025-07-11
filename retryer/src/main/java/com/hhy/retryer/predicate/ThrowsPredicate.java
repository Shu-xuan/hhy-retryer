package com.hhy.retryer.predicate;

/**
 * <p>
 * 描述: 抛异常断言
 * </p>
 *
 * @Author hhy
 */
public class ThrowsPredicate<T> implements IPredicate<T> {
    /**
     * 预期抛出的异常
     */
    private final Class<? extends Throwable> expectedThrowable;

    public ThrowsPredicate(Class<? extends Throwable> expectedThrowable) {
        this.expectedThrowable = expectedThrowable;
    }

    @Override
    public boolean apply(T input) {
        return expectedThrowable.equals(input.getClass());
    }
}
