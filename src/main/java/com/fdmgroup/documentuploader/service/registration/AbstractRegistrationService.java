package com.fdmgroup.documentuploader.service.registration;

import com.fdmgroup.documentuploader.dto.register.RegisterDto;
import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to the
 * registration of a new {@link User} and their {@link Account}
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractRegistrationService {

	/**
	 * Registers a new account based on the objects encapsulated within the
	 * {@link RegisterDto}.
	 * 
	 * @param registerDto the information to use when registering a new account
	 * @return the registered {@link User} instance
	 * @throws PasswordsDoNotMatchException when passwords contained in the given
	 *                                      {@code registerDto} do not match.
	 */
	User registerNewAccount(RegisterDto registerDto);

}
