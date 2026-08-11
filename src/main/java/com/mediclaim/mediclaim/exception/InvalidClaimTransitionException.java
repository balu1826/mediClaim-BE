package com.mediclaim.mediclaim.exception;

import org.springframework.http.HttpStatus;

public class InvalidClaimTransitionException extends RuntimeException {

	private final HttpStatus status;

	public InvalidClaimTransitionException(String message) {
		this(message, HttpStatus.BAD_REQUEST);
	}

	public InvalidClaimTransitionException(String message, HttpStatus status) {

		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}