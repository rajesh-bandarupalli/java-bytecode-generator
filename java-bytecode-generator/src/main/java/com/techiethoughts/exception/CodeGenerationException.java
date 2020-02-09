package com.techiethoughts.exception;

/**
 * @author Rajesh Bandarupalli
 */

public class CodeGenerationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CodeGenerationException(String message) {
		super(message);
	}
	
	public CodeGenerationException(Throwable cause) {
		super(cause);
	}

	public CodeGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
