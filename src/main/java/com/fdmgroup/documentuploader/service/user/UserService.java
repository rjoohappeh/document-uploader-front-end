package com.fdmgroup.documentuploader.service.user;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.user.AbstractUserApiService;

/**
 * <p>
 * Service class which contains methods that submit HTTP Requests to and HTTP
 * Responses from the REST API for {@link User} based operations.
 * </p>
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class UserService implements AbstractUserService {

	/**
	 * Used to perform API calls to save, update, read, or remove {@link Document}
	 * objects.
	 */
	private final AbstractUserApiService userApiService;

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;

	/**
	 * Used to encode passwords given by the client.
	 */
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(AbstractUserApiService userApiService, MessageSource messageSource,
					   PasswordEncoder passwordEncoder) {
		super();
		this.userApiService = userApiService;
		this.messageSource = messageSource;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User updatePassword(User user, String currentPassword, String newPassword, String newPasswordConfirmation) {
		if (!newPasswordMatchesConfirmation(newPassword, newPasswordConfirmation)) {
			throw new PasswordsDoNotMatchException(
					messageSource.getMessage("password.must-match", null, Locale.getDefault()));
		}

		String userPasswordEncoded = user.getPassword();
		if (!passwordEncoder.matches(currentPassword, userPasswordEncoded)) {
			throw new PasswordsDoNotMatchException(
					messageSource.getMessage("user.incorrect-password", null, Locale.getDefault()));
		}

		String newPasswordEncoded = passwordEncoder.encode(newPassword);
		user.setPassword(newPasswordEncoded);
		userApiService.update(user).subscribe();

		return user;
	}

	/**
	 * Checks if {@code newPassword} equals {@code newPasswordConfirmation}.
	 * 
	 * @param newPassword             the value given by the client for the new
	 *                                password of a {@link User}
	 * @param newPasswordConfirmation the confirmation of {@code newPassword}
	 * @return {@code true} if {@code newPassword} equals
	 *         {@code newPasswordConfirmation} and {@code false} otherwise
	 */
	private boolean newPasswordMatchesConfirmation(String newPassword, String newPasswordConfirmation) {
		return newPassword.equals(newPasswordConfirmation);
	}

	@Override
	public Optional<User> findById(long id) {
		return userApiService.findById(id);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userApiService.findByEmail(email);
	}

	@Override
	public boolean hasActivatedAccount(String email) {
		return userApiService.isEnabled(email);
	}

	@Override
	public void processResetPasswordRequest(String email) {
		userApiService.resetPassword(email);
	}

	@Override
	public boolean isValidPasswordResetToken(String passwordResetToken) {
		return userApiService.isValidPasswordResetToken(passwordResetToken);
	}

	@Override
	public void resetPassword(String email, String newPassword, String newPasswordConfirmation,
			String passwordResetToken) {
		if (!newPasswordMatchesConfirmation(newPassword, newPasswordConfirmation)) {
			throw new PasswordsDoNotMatchException(messageSource.getMessage(
					"password.must-match", null, Locale.getDefault()));
		}
		String encodedPassword = passwordEncoder.encode(newPassword);
		userApiService.changePassword(email, encodedPassword, passwordResetToken);
	}
}
