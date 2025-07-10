package com.hhy.retryer.predicate;

/**
 * <p>
 * 描述: 抛异常断言
 * </p>
 *
 * @Author hhy
 */
public class ThrowsPredicate<T> implements IPredicate<T> {
    private final Class<? extends Throwable> throwable;

    public ThrowsPredicate(Class<? extends Throwable> throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean apply(T input) {
        return throwable.equals(input);
    }
}
