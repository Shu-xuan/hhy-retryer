package com.hhy.retryer.strategy;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 指数退避策略：
 * 顾名思义，是一种在发生访问冲突或失败时，通过逐步增加等待时间（按指数级增长）来重试的机制。
 * 这种策略旨在减少对服务器的压力，同时提高重试的成功率。
 *
 * 具体来说，当你首次遇到访问失败时，你会等待一个较短的时间（如1秒）后再进行重试。
 * 如果仍然失败，则等待时间加倍（如2秒），再失败则继续加倍（如4秒），以此类推。这样，随着失败次数的增加，重试的间隔也会呈指数级增长。
 * </p>
 *
 * @Author hhy
 */
public class ExponentialRetryerStrategy extends RetryerStrategy{

    private int BASE = 1;

    public ExponentialRetryerStrategy(int count, int time, TimeUnit timeUnit) {
        super(count, time, timeUnit);
    }

    @Override
    public void reset() {
        this.BASE = 1;
    }

    @Override
    protected boolean exec() {
        while (count >= BASE) {
            try {
                long waitTime = (long)(time * Math.pow(2, BASE++ - 1));
                timeUnit.sleep(waitTime);
                return true;
            } catch (InterruptedException e) {
                // 等待期间被中断，抛异常
                Thread.currentThread().interrupt();
                throw new RuntimeException("重试中断...", e);
            }
        }
        return false;
    }
}
