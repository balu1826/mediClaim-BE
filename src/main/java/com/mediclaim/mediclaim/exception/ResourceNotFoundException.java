package com.mediclaim.mediclaim.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {
	private final HttpStatus status;

	public ResourceNotFoundException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}

	public ResourceNotFoundException(String message) {
		this(message, HttpStatus.BAD_REQUEST);
	}

	public HttpStatus getStatus() {
		return status;
	}

}
