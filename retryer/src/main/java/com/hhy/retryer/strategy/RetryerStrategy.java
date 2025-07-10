package com.hhy.retryer.strategy;

import com.hhy.retryer.listener.RetryerListener;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 重试策略抽象类
 * </p>
 *
 * @Author hhy
 */
public abstract class RetryerStrategy {
    /**
     * 重试次数
     */
    protected int count;

    /**
     * 每次等待时间
     */
    protected final int time;

    /**
     * 时间单位
     */
    protected final TimeUnit timeUnit;

    public RetryerStrategy(int count, int time, TimeUnit timeUnit) {
        this.count = count;
        this.time = time;
        this.timeUnit = timeUnit;
    }

    protected RetryerListener retryerListener;

    public void setRetryerListener(RetryerListener retryerListener) {
        this.retryerListener = retryerListener;
    }

    /**
     * 重置重试次数
     */
    public abstract void reset();

    protected abstract boolean exec();

    public boolean exec0() {
        final boolean result = exec();
        if (result) {
            // 执行回调
            triggerRetryerListener();
        }
        return result;
    }

    void triggerRetryerListener() {
        if (null != retryerListener) {
            retryerListener.exec(this);
        }
    }

    public int getCount() {
        return count;
    }

    public int getTime() {
        return time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
