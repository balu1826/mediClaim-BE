package com.mediclaim.mediclaim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class FraudExecutorConfig {

	@Bean(name = "fraudTaskExecutor")
	public Executor fraudTaskExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(20);
		executor.setThreadNamePrefix("fraud-rule-");

		executor.initialize();

		return executor;
	}
}
