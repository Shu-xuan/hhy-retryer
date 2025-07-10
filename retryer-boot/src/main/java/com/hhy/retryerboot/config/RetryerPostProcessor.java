package com.hhy.retryerboot.config;

import com.hhy.retryer.core.Retryer;
import com.hhy.retryer.listener.RetryerListener;
import com.hhy.retryer.predicate.MyPredicate;
import com.hhy.retryer.strategy.ExponentialRetryerStrategy;
import com.hhy.retryer.strategy.RetryerStrategy;
import com.hhy.retryerboot.proxy.RetryerCGLibProxyFactory;
import com.hhy.retryerboot.proxy.RetryerMethodInterceptor;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * <p>
 * 描述: 创建代理对象
 * </p>
 *
 * @Author hhy
 */
public class RetryerPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();
        Method[] methods = beanClass.getDeclaredMethods();
        Map<Integer, Retryer> retryerMap = new HashMap<>();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(com.hhy.retryerboot.annotation.Retryer.class)) {
                continue;
            }
            System.out.println(method.getDeclaringClass() + "#" + method.getName() + " 存在 [Retryer] 注解");
            try {
                Retryer retryer = defaultRetryerStrategyMatchRule(method);
                if (retryer == null) {
                    continue;
                }
                retryerMap.put(method.hashCode(), retryer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 生成代理
        if (!retryerMap.isEmpty()) {
            RetryerMethodInterceptor interceptor = new RetryerMethodInterceptor(bean, retryerMap);
            bean = RetryerCGLibProxyFactory.createProxy(beanClass, interceptor);
        }
        return bean;
    }


    protected Retryer defaultRetryerStrategyMatchRule(Method method) throws Exception {

        final com.hhy.retryerboot.annotation.Retryer retryerAnnotation = method.getAnnotation(com.hhy.retryerboot.annotation.Retryer.class);

        final String[] result = retryerAnnotation.result();
        final Class[] resultClass = retryerAnnotation.resultClass();
        if (result.length != resultClass.length) {
            throw new WrongNumberArgsException("结果参数长度与类型类型长度不匹配");
        }

        final int count = retryerAnnotation.maxAttempts();
        final int time = retryerAnnotation.time();
        final TimeUnit timeUnit = retryerAnnotation.timeUnit();
        final String listenerName = retryerAnnotation.listenerName();
        final Class[] throwsColl = retryerAnnotation.exception();
        final String taskExecutorName = retryerAnnotation.taskExecutorName();
        final Predicate[] resultPredicates = new Predicate[result.length];

        RetryerStrategy retryerStrategy = new ExponentialRetryerStrategy(count, time, timeUnit);
        for (int i = 0; i < result.length; i++) {
            try {
                resultPredicates[i] = MyPredicate.equalsTo(resultClass[i].getConstructor(String.class).newInstance(result[i]));
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        final Predicate resultPredicateObject = MyPredicate.or(resultPredicates);

        final Predicate[] throwsPredicates = new Predicate[throwsColl.length];
        for (int i = 0; i < throwsPredicates.length; i++) {
            throwsPredicates[i] = MyPredicate.throwsTo(throwsColl[i]);
        }
        final Predicate throwsResultPredicateObject = MyPredicate.or(throwsPredicates);

        // 线程池
        Object threadPoll = null;
        if (!"".equals(taskExecutorName)) {
            threadPoll = applicationContext.getBean(taskExecutorName);
        }

        // 监听器
        Object listerBean = null;
        if (!"".equals(listenerName)) {
            listerBean = applicationContext.getBean(listenerName);
        }

        if (listerBean != null && !(listerBean instanceof RetryerListener)) {
            throw new ClassCastException(listerBean + "不是 RetryerListener 子类");
        }

        if (threadPoll != null && !(threadPoll instanceof Executor)) {
            throw new ClassCastException(threadPoll + "不是 Executor 子类");
        }
        System.out.println(method.getDeclaringClass() + "#" + method.getName() + " 构建 [Retryer] 完毕");
        return new Retryer(retryerStrategy, resultPredicateObject, throwsResultPredicateObject, (RetryerListener) listerBean, (Executor) threadPoll);
    }
}
