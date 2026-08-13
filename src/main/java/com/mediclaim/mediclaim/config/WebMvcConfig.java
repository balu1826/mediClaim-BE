package com.mediclaim.mediclaim.config;

import com.mediclaim.mediclaim.annotaion.TenantOwnershipInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final TenantOwnershipInterceptor tenantOwnershipInterceptor;

	public WebMvcConfig(TenantOwnershipInterceptor tenantOwnershipInterceptor) {

		this.tenantOwnershipInterceptor = tenantOwnershipInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(tenantOwnershipInterceptor);
	}
}