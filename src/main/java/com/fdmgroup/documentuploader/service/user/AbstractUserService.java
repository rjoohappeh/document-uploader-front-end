package com.fdmgroup.documentuploader.service.user;

import java.util.Optional;

import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to the
 * modification, retrieval, addition, or removal of {@link User} instances in
 * the system.
 * </p>
 * 
 * @author Noah Anderson
 *
 */
public interface AbstractUserService {

	/**
	 * Updates the password of the given {@link User} object to the value of
	 * {@code newPassword}.
	 * 
	 * @param user                    the {@code User} object to update
	 * @param currentPassword         the entered value for the current password of
	 *                                the given {@code User}
	 * @param newPassword             the new password for the given {@code User}
	 * @param newPasswordConfirmation the confirmation of the new password
	 * @return the updated {@code User} instance
	 * @throws PasswordsDoNotMatchException when {@code newPassword} and
	 *                                      {@code newPasswordConfirmation} are not
	 *                                      equal or {@code currentPassword} does
	 *                                      not match the decoded value of the given
	 *                                      {@code User}'s password
	 */
	User updatePassword(User user, String currentPassword, String newPassword, String newPasswordConfirmation);

	/**
	 * Retrieves an {@link Optional} encapsulating a {@link User} instance.
	 * 
	 * @param id the {@code id} to search for a {@code User} object with
	 * @return empty {@code Optional} if no User is found with the given {@code id},
	 *         <br/>
	 *         {@code Optional} encapsulating the found {@code User} object
	 *         otherwise
	 */
	Optional<User> findById(long id);

	/**
	 * Retrieves an {@link Optional} encapsulating a {@link User} instance.
	 * 
	 * @param email the {@code email} to search for a {@code User} object with
	 * @return empty {@code Optional} if no User is found with the given
	 *         {@code email}, <br/>
	 *         {@code Optional} encapsulating the found {@code User} object
	 *         otherwise
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Checks if a {@link User} with the given {@code email} has activated their
	 * account .
	 * 
	 * @param email id the {@code id} to search for a {@code User} object with
	 * @return {@code true} if the {@code User} has activated their account and
	 *         {@code false} otherwise.
	 */
	boolean hasActivatedAccount(String email);

	/**
	 * Handles the logic behind the process of initiating a request to reset the
	 * password of a {@link User} with an {@code email} equaling the {@code email}
	 * given.
	 *
	 * @param email the {@code email} of a {@code User}
	 */
	void processResetPasswordRequest(String email);

	/**
	 * Checks if the value of the given {@code passwordResetToken} is valid.
	 * 
	 * @param passwordResetToken the token which may be valid
	 * @return {@code false} if the given {@code passwordResetToken} has expired or
	 *         does not exist. Otherwise, returns {@code true}.
	 */
	boolean isValidPasswordResetToken(String passwordResetToken);

	/**
	 * Attempts to reset the password of a {@link User} with the given
	 * {@code email}.
	 * 
	 * @param email                   the {@code email} of a {@code User}
	 * @param newPassword             the new {@code password} of a {@code User}
	 *                                with an email equal to the given {@code email}
	 * @param newPasswordConfirmation the confirmation of the {@code newPassword}
	 * @param passwordResetToken      the token given to the {@code User} when they
	 *                                requested to reset their {@code password}
	 * @throws PasswordsDoNotMatchException when {@code newPassword} and
	 *                                      {@code newPasswordConfirmation} do not
	 *                                      match
	 */
	void resetPassword(String email, String newPassword, String newPasswordConfirmation, String passwordResetToken);
}
