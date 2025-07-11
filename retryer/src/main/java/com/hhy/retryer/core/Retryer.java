package com.hhy.retryer.core;

import com.hhy.retryer.listener.RetryerListener;
import com.hhy.retryer.predicate.IPredicate;
import com.hhy.retryer.strategy.RetryerStrategy;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

/**
 * <p>
 * 描述: 封装
 * 支持建造者模式
 * </p>
 *
 * @Author hhy
 */
public class Retryer {
    /**
     * 重试策略
     */
    private RetryerStrategy retryerStrategy;

    /**
     * 断言结果
     */
    private Predicate predicateResult;

    /**
     * 断言抛出
     */
    private Predicate predicateThrowsException;

    /**
     * 执行器
     */
    private Executor executor;

    public Retryer(RetryerStrategy retryerStrategy, Predicate predicateResult,
                   Predicate predicateThrowsException, RetryerListener retryerListener, Executor executor) {
        this.retryerStrategy = retryerStrategy;
        this.retryerStrategy.setRetryerListener(retryerListener);
        this.predicateResult = predicateResult;
        this.predicateThrowsException = predicateThrowsException;
        this.executor = executor;
    }

    /**
     * 通过传入代码块的方式执行
     */
    public <T> T exec(RetryerSupplier<T> supplier) {
        T result = null;
        try {
            // 执行原始任务，获取返回结果
            result = supplier.exec();
            if (shouldRetry(result)) {
                // 传入了线程池就异步
                if (null != executor) {
                    executor.execute(() -> {
                        if (execRetryStrategy()) {
                            asyncExec(supplier);
                        }
                    });
                } else {
                    if (execRetryStrategy()) {
                        exec(supplier);
                    }
                }
            }
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                // 可能异常被封装，获取真实异常
                // RuntimeException 没有包装其他异常时，这里返回的就是 null
                throwable = throwable.getCause();
            }
            if (shouldRetry(throwable)) {
                if (null != executor) {
                    executor.execute(() -> {
                        if (execRetryStrategy()) {
                            asyncExec(supplier);
                        }
                    });
                } else {
                    if (execRetryStrategy()) {
                        exec(supplier);
                    }
                }
            }
        }
        reset();
        return result;
    }

    private <T> void asyncExec(RetryerSupplier<T> supplier) {
        try {
            // 执行原始逻辑
            T result = supplier.exec();
            if (shouldRetry(result)) {
                if (execRetryStrategy()) {
                    asyncExec(supplier);
                }
            }
        } catch (Throwable throwable) {
            // 可能异常被封装，获取真实异常
            if (throwable instanceof RuntimeException) {
                throwable = throwable.getCause();
            }
            if (shouldRetry(throwable)) {
                if (execRetryStrategy()) {
                    asyncExec(supplier);
                }
            }
        }
    }

    private boolean execRetryStrategy() {
        return retryerStrategy.exec0();
    }
    private boolean shouldRetry(Object result) {
        return null != predicateResult && ((IPredicate) predicateResult).apply(result);
    }

    // 异常判断重试
    private boolean shouldRetry(Throwable e) {
        return null != predicateThrowsException && ((IPredicate) predicateThrowsException).apply(e);
    }

    private void reset(){
        retryerStrategy.reset();
    }

    public static class Builder {
        private RetryerStrategy retryerStrategy;

        private Predicate<Object> predicateResult;

        private Predicate<Throwable> predicateThrowsException;

        private RetryerListener retryerListener;

        private ExecutorService executorService;

        public static Builder builder(){
            return new Builder();
        }

        public Builder retryerStrategy(RetryerStrategy retryerStrategy){
            this.retryerStrategy = retryerStrategy;
            return this;
        }

        public Builder retryerIfThrowsException(Predicate predicate){
            this.predicateThrowsException = predicate;
            return this;
        }

        public Builder retryerIfResult(Predicate predicate){
            this.predicateResult = predicate;
            return this;
        }

        public Builder retryerExecutor(ExecutorService executorService){
            this.executorService = executorService;
            return this;
        }

        public Builder retryerListener(RetryerListener retryerListener){
            this.retryerListener = retryerListener;
            return this;
        }

        public Retryer build() {
            return new Retryer(retryerStrategy, predicateResult, predicateThrowsException, retryerListener, executorService);
        }
    }

}
