package com.hhy.retryer.predicate;

import java.util.List;
import java.util.function.Predicate;

/**
 * <p>
 * 描述: 满足任何满足一个即可
 * </p>
 *
 * @Author hhy
 */
public class AnyPredicate implements IPredicate {
    private final List<Predicate> predicates;

    public AnyPredicate(List<Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean apply(Object input) {
        for (Predicate predicate : predicates) {
            // 用传入的所有断言去匹配，有一个匹配就行
            if (((IPredicate)predicate).apply(input)) {
                return true;
            }
        }
        return false;
    }
}
