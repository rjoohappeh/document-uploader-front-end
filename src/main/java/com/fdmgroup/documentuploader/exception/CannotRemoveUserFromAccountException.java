package com.fdmgroup.documentuploader.exception;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * Exception thrown when a {@link User} cannot be removed from an
 * {@link Account} for any reason.
 * 
 * @author Noah Anderson
 *
 */
public class CannotRemoveUserFromAccountException extends RuntimeException {

	private static final long serialVersionUID = -4650760545532205558L;

	public CannotRemoveUserFromAccountException() {
		super();
	}

	public CannotRemoveUserFromAccountException(String message) {
		super(message);
	}

}
