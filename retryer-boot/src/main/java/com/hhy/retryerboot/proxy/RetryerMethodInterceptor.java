package com.hhy.retryerboot.proxy;

import com.hhy.retryer.core.Retryer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>
 * 描述: 重试器方法拦截器
 * </p>
 *
 * @Author hhy
 */
public class RetryerMethodInterceptor implements MethodInterceptor {
    private Object target;

    /**
     * 需要被重试的方法的映射表
     */
    private Map<Integer, Retryer> retryerHashMap;

    public RetryerMethodInterceptor() {}

    public RetryerMethodInterceptor(Object target, Map<Integer, Retryer> retryerHashMap) {
        this.target = target;
        this.retryerHashMap = retryerHashMap;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        final int hashCode = method.hashCode();
        // 需要重试的方法则进行重试
        if (retryerHashMap.containsKey(hashCode)) {
//            System.out.println(method.getDeclaringClass() + "#" + method.getName() + " 进行 [Retryer] 重试");
            // 交给重试器去执行
            return retryerHashMap.get(hashCode).exec(() -> {
                final Object result;
                try {
                    result = methodProxy.invokeSuper(o, objects);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                return result;
            });
        }

        // 直接执行目标方法
        return methodProxy.invokeSuper(o, objects);
    }

}
