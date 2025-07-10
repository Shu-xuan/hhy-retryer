package com.hhy.retryer.predicate;

/**
 * <p>
 * 描述: 相等断言
 * </p>
 *
 * @Author hhy
 */
public class EqualPredicate<T> implements IPredicate {
    private final Object object;

    public EqualPredicate(Object object) {
        this.object = object;
    }

    @Override
    public boolean apply(Object input) {
        return object.equals(input);
    }
}
