package org.pms.infrastructure.adapter.port;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author alcsyooterranf
 * @description Feign客户端配置, 配置超时、重试、日志等
 */
@Configuration
public class FeignConfig {
	
	/**
	 * Feign日志级别
	 * NONE: 不记录日志
	 * BASIC: 仅记录请求方法、URL、响应状态码和执行时间
	 * HEADERS: 记录BASIC级别的基础上，记录请求和响应的header
	 * FULL: 记录请求和响应的header、body和元数据
	 */
	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.BASIC;
	}
	
	/**
	 * Feign请求超时配置
	 * connectTimeout: 连接超时时间
	 * readTimeout: 读取超时时间
	 */
	@Bean
	public Request.Options options() {
		return new Request.Options(
				5000, TimeUnit.MILLISECONDS,  // 连接超时5秒
				10000, TimeUnit.MILLISECONDS, // 读取超时10秒
				true                           // 跟随重定向
		);
	}
	
	/**
	 * Feign重试配置
	 * period: 重试间隔
	 * maxPeriod: 最大重试间隔
	 * maxAttempts: 最大重试次数
	 */
	@Bean
	public Retryer retryer() {
		// 重试间隔100ms，最大间隔1000ms，最多重试3次
		return new Retryer.Default(100, 1000, 3);
	}
	
}

