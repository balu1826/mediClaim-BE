package com.mediclaim.mediclaim.annotaion;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class TenantOwnershipInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		TenantOwned tenantOwned = handlerMethod.getMethodAnnotation(TenantOwned.class);

		if (tenantOwned == null) {
			return true;
		}

		// Tenant ownership logic will go here.
		// This is only for sample
		@SuppressWarnings("unchecked")
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		String idValue = pathVariables.get(tenantOwned.paramId());

		if (idValue == null) {
			throw new IllegalArgumentException("Resource ID not found: " + tenantOwned.paramId());
		}

		return true;
	}
}