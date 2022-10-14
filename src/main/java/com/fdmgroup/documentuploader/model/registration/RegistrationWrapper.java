package com.fdmgroup.documentuploader.model.registration;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * Encapsulates the {@link User}, {@link Account}, and {@link AuthGroup} objects 
 * which are sent to the REST API upon successful validation of user input.
 * 
 * @author Noah Anderson
 *
 */
public class RegistrationWrapper {

	private User user;
	private Account account;
	private AuthGroup authGroup;

	public RegistrationWrapper() {
		super();
	}

	public RegistrationWrapper(User user, Account account, AuthGroup authGroup) {
		super();
		this.user = user;
		this.account = account;
		this.authGroup = authGroup;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AuthGroup getAuthGroup() {
		return authGroup;
	}

	public void setAuthGroup(AuthGroup authGroup) {
		this.authGroup = authGroup;
	}

}
