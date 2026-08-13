package com.mediclaim.mediclaim.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

	private static final String CORRELATION_ID = "X-Correlation-ID";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String correlationId = request.getHeader(CORRELATION_ID);

		if (correlationId == null || correlationId.isBlank()) {

			correlationId = UUID.randomUUID().toString();
		}

		try {

			// Store correlation ID for logging
			MDC.put("correlationId", correlationId);

			// Return it to the client
			response.setHeader(CORRELATION_ID, correlationId);

			// Continue request processing
			filterChain.doFilter(request, response);

		} finally {

			// Prevent MDC data from leaking
			// to another request/thread.
			MDC.remove("correlationId");
		}
	}
}