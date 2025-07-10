package com.hhy.retryer.predicate;

import java.util.function.Predicate;

/**
 * <p>
 * 描述: 充实条件断言
 * 不喜欢 Predicate#test，包一层
 * </p>
 *
 * @Author hhy
 */
public interface IPredicate<T> extends Predicate<T> {
    boolean apply(T input);

    @Override
    default boolean test(T t) {
        return this.apply(t);
    }
}
