package com.fdmgroup.documentuploader.exception;

/**
 * Exception thrown when passwords given from the client application
 * fail an equality check.
 * 
 * @author Noah Anderson
 *
 */
public class PasswordsDoNotMatchException extends RuntimeException {

	private static final long serialVersionUID = -5993282542380630321L;

	public PasswordsDoNotMatchException() {
		super();
	}

	public PasswordsDoNotMatchException(String message) {
		super(message);
	}

}
