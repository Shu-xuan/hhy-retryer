package com.hhy.retryerboot.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * <p>
 * 描述: 定制 cglib 代理工厂
 * </p>
 *
 * @Author hhy
 */
public class RetryerCGLibProxyFactory {
    /**
     * 创建代理对象
     * @param toProxy 要代理的目标类
     * @param methodInterceptor 方法拦截器
     */
    public static Object createProxy(Class<?> toProxy, MethodInterceptor methodInterceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(toProxy);
        enhancer.setCallback(methodInterceptor);
        return enhancer.create();
    }
}
