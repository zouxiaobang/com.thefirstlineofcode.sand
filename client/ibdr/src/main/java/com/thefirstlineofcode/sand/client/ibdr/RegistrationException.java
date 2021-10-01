package com.thefirstlineofcode.sand.client.ibdr;

public class RegistrationException extends Exception {
	
	private static final long serialVersionUID = 3398560808264161877L;
	
	private IbdrError error;
	private Throwable cause;
	
	public RegistrationException(IbdrError error) {
		this(error, null);
	}
	
	public RegistrationException(IbdrError error, Throwable cause) {
		this.error = error;
		this.cause = cause;
	}

	public IbdrError getError() {
		return error;
	}

	public Throwable getCause() {
		return cause;
	}
	
	@Override
	public String getMessage() {
		return error.toString();
	}
	
}
