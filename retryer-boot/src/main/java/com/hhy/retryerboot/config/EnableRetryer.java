package com.hhy.retryerboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * 描述: 导入重启器服务
 * </p>
 *
 * @Author hhy
 */
@Configuration
@Import(RetryerPostProcessor.class)
public @interface EnableRetryer {


}