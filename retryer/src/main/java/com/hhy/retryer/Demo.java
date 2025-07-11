package com.hhy.retryer;

import com.hhy.retryer.core.Retryer;
import com.hhy.retryer.predicate.MyPredicate;
import com.hhy.retryer.strategy.TimeRetryerStrategy;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 测试
 * </p>
 *
 * @Author hhy
 */
public class Demo {
    static volatile Boolean flag = true;


    public static void main(String[] args) {
        testThrow();

    }

    private static void testEqual() {
        final Retryer retryer = new Retryer.Builder()
                .retryerStrategy(new TimeRetryerStrategy(3, 1, TimeUnit.SECONDS))
                .retryerIfResult(MyPredicate.equalsTo(true)) // 拦截返回 true 的结果并且重试
                .retryerListener(retryerStrategy -> {
                    System.out.println("监听器回调 - " + retryerStrategy.getCount());
                })
                .build();

        new Thread(() -> {
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            flag = false;
            System.out.println("一秒后 - " + flag);
        }).start();

        retryer.exec(() -> {
            System.out.println("hello1 hhy-retryer");
            System.out.println("一秒前 - " + flag);
            return flag;
        });

        flag = true;
        retryer.exec(() -> {
            System.out.println("hello2 hhy-retryer");
            System.out.println("我在里面" + flag);
            return flag;
        });

    }

    private static void testThrow() {
        final Retryer retryer = new Retryer.Builder()
                .retryerStrategy(new TimeRetryerStrategy(3, 1, TimeUnit.SECONDS))
                .retryerIfThrowsException(MyPredicate.throwsTo(RuntimeException.class)) // 拦截返回 true 的结果并且重试
                .retryerListener(retryerStrategy -> {
                    System.out.println("监听器回调 - " + retryerStrategy.getCount());
                })
                .retryerExecutor(Executors.newFixedThreadPool(1))
                .build();

        retryer.exec(() -> {
            System.out.println("一次执行");
            throw new RuntimeException(new RuntimeException("巴拉巴拉"));
        });

    }
}
