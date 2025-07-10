package com.hhy.retryer.strategy;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 根据重试时间
 * </p>
 *
 * @Author hhy
 */
public class TimeRetryerStrategy extends RetryerStrategy{
    private final int maxCount;

    public TimeRetryerStrategy(int count, int time, TimeUnit timeUnit) {
        super(count, time, timeUnit);
        this.maxCount = count;
    }

    @Override
    public void reset() {
        this.count = maxCount;
    }

    @Override
    protected boolean exec() {
        while (count-- > 0) {
            try {
                timeUnit.sleep(time);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("重试中断..." ,e);
            }
        }
        return false;
    }
}
