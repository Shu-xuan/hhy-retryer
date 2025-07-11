package com.hhy.retryer.predicate;

/**
 * <p>
 * 描述: 相等断言
 * </p>
 *
 * @Author hhy
 */
public class EqualPredicate<T> implements IPredicate {
    /**
     * 预期值
     */
    private final Object expectedVal;

    public EqualPredicate(Object expectedVal) {
        this.expectedVal = expectedVal;
    }

    @Override
    public boolean apply(Object input) {
        // 断言与传入的结果匹配
        return expectedVal.equals(input);
    }
}
