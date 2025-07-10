package com.hhy.retryer.listener;

import com.hhy.retryer.strategy.RetryerStrategy;

/**
 * <p>
 * 描述: 标注为函数式接口，可以使用 Lambda 表达式或方法引用赋值
 * 监听器可以在重试过程中提供更好的可控性和拓展性，供后续分析重试，日志打印，通知等行为
 * </p>
 *
 * @Author hhy
 */
@FunctionalInterface
public interface RetryerListener {
    void exec(RetryerStrategy retryerStrategy);
}
