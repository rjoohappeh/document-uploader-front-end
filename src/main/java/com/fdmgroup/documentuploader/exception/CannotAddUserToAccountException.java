package com.fdmgroup.documentuploader.exception;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * Exception thrown when a {@link User} may not be added to an {@link Account}
 * for any reason.
 * 
 * @author Noah Anderson
 *
 */
public class CannotAddUserToAccountException extends RuntimeException {

	private static final long serialVersionUID = -882425818444903407L;

	public CannotAddUserToAccountException() {
		super();
	}

	public CannotAddUserToAccountException(String message) {
		super(message);
	}

}
