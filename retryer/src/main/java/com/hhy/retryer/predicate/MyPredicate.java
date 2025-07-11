package com.hhy.retryer.predicate;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * <p>
 * 描述: 对外
 * </p>
 *
 * @Author hhy
 */
public class MyPredicate<T> {
    public static Predicate throwsTo(Class<? extends Throwable> throwable) {
        return new ThrowsPredicate(throwable);
    }
    public static Predicate equalsTo(Object expectedVal) {
        return new EqualPredicate(expectedVal);
    }

    public static Predicate anyOf(Predicate... predicates) {
        if (null == predicates || predicates.length == 0){
            return null;
        }
        return new AnyPredicate(Arrays.asList(predicates));
    }
}
