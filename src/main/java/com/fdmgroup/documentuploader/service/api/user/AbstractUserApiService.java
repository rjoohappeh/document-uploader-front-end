package com.fdmgroup.documentuploader.service.api.user;

import java.util.Optional;

import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.AbstractApiService;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to the
 * modification, retrieval, addition, or removal of {@link User} instances in
 * the system.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractUserApiService extends AbstractApiService<User, Long> {

	/**
	 * Retrieves a {@link User} instance with an {@code email} equaling the given
	 * {@code email}.
	 * 
	 * @param email the {@code email} of a {@code User}
	 * @return {@code empty} {@link Optional} when the given {@code email} is not
	 *         associated with any {@code User} instance. Otherwise, a
	 *         {@code Optional} wrapping the found {@code User} is returned.
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Checks if the {@link User} with the given {@code email} has activated their
	 * account.
	 * 
	 * @param email the {@code email} of a {@code User}
	 * @return {@code true} if they have activated their account and {@code false}
	 *         if they have not
	 */
	boolean isEnabled(String email);

	/**
	 * Initiates the process of resetting the password of a {@link User} with an
	 * {@code email} matching the given {@code email}.
	 *
	 * @param email the {@code email} of a {@code User}
	 */
	void resetPassword(String email);

	/**
	 * Checks if the value of the given {@code passwordResetToken} is valid.
	 *
	 * @param passwordResetToken the token which may be valid
	 * @return {@code false} if the given {@code passwordResetToken} has expired or
	 *         does not exist. Otherwise, returns {@code true}.
	 */
	boolean isValidPasswordResetToken(String passwordResetToken);

	/**
	 * Changes the password of a {@link User} with an email equaling the given
	 * {@code email} to the value of {@code newPassword}.
	 *
	 * @param email              the {@code email} of a {@code User}
	 * @param newPassword        the new {@code password} of a {@code User} * with
	 *                           an email equal to the given * {@code email}
	 * @param passwordResetToken the token given to the {@code User} when they *
	 *                           requested to reset their {@code password}
	 */
	void changePassword(String email, String newPassword, String passwordResetToken);
}
