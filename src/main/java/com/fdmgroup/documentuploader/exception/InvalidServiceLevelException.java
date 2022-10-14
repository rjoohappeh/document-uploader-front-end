package com.fdmgroup.documentuploader.exception;

import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;

/**
 * Exception thrown when an invalid {@link ServiceLevel} is selected for any
 * reason.
 * 
 * @author Noah Anderson
 *
 */
public class InvalidServiceLevelException extends RuntimeException {

	private static final long serialVersionUID = 4181999160980851397L;

	public InvalidServiceLevelException() {
		super();
	}

	public InvalidServiceLevelException(String message) {
		super(message);
	}

}
