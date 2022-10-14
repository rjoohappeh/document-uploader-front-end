package com.fdmgroup.documentuploader.service;

import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.user.UserApiService;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

	private static final String ENCODED_PASSWORD = "encodedPassword";
	private static final String CURRENT_PASSWORD = "currentPassword";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String BAD_CONFIRMATION = "badConfirmation";
	private static final String TEST_EMAIL = "testEmail@email.com";
	private static final String TEST_TOKEN = "testToken123";

	private UserService userService;

	@Mock
	private MessageSource mockMessageSource;

	@Mock
	private BCryptPasswordEncoder mockPasswordEncoder;

	@Mock
	private User mockUser;

	@Mock
	private UserApiService mockUserApiService;

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.userService = new UserService(mockUserApiService, mockMessageSource, mockPasswordEncoder);
	}

	@Test
	void testUpdatePassword_throwsPasswordsDoNotMatchException_whenNewPassword_doesNotMatch_newPasswordConfirmation() {
		assertThrows(PasswordsDoNotMatchException.class,
				() -> userService.updatePassword(mockUser, CURRENT_PASSWORD, NEW_PASSWORD, BAD_CONFIRMATION));
	}

	@Test
	void testUpdatePassword_throwsPasswordsDoNotMatchException_whenEncodedCurrentPassword_doesNotMatch_theCurrentPasswordOfUser() {
		when(mockUser.getPassword()).thenReturn(NEW_PASSWORD);
		when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

		assertThrows(PasswordsDoNotMatchException.class,
				() -> userService.updatePassword(mockUser, CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD));
	}

	@Test
	void testUpdatePassword_returnsUserWithUpdatedPassword_whenCurrentPasswordIsCorrect_and_newPasswordMatchesNewPasswordConfirmation()
			throws PasswordsDoNotMatchException {
		when(mockUser.getPassword()).thenReturn(NEW_PASSWORD);
		when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(mockPasswordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
		when(mockUserApiService.update(mockUser)).thenReturn(Mono.just(mockUser));

		userService.updatePassword(mockUser, CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

		verify(mockUser).setPassword(ENCODED_PASSWORD);
	}

	@Test
	void testProcessResetPasswordRequest_callsUserApiServiceResetPassword() {
		userService.processResetPasswordRequest(TEST_EMAIL);

		verify(mockUserApiService, times(1)).resetPassword(TEST_EMAIL);
	}

	@Test
	void testIsValidPasswordResetToken_callsUserApiServiceIsValidPasswordResetToken() {
		userService.isValidPasswordResetToken(TEST_EMAIL);

		verify(mockUserApiService, times(1)).isValidPasswordResetToken(TEST_EMAIL);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void testIsValidPasswordResetToken_returnsResultFromUserApiServiceIsValidPasswordResetToken(boolean expected) {
		when(mockUserApiService.isValidPasswordResetToken(TEST_EMAIL)).thenReturn(expected);

		boolean actual = userService.isValidPasswordResetToken(TEST_EMAIL);
		assertEquals(expected, actual);
	}

	@Test
	void testResetPassword_throwsPasswordsDoNotMatchException_whenNewPasswordDoesNotMatchConfirmation() throws PasswordsDoNotMatchException {
		Assertions.assertThrows(PasswordsDoNotMatchException.class, () ->
				userService.resetPassword(TEST_EMAIL, NEW_PASSWORD, BAD_CONFIRMATION, TEST_TOKEN));
	}
	
	@Test
	void testResetPassword_callsPasswordEncoderEncode_whenNoExceptionIsThrown() throws PasswordsDoNotMatchException {
		userService.resetPassword(TEST_EMAIL, NEW_PASSWORD, NEW_PASSWORD, TEST_TOKEN);

		verify(mockPasswordEncoder, times(1)).encode(NEW_PASSWORD);
	}

	@Test
	void testResetPassword_callsUserApiServiceChangePassword_whenNoExceptionIsThrown() throws PasswordsDoNotMatchException {
		when(mockPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

		userService.resetPassword(TEST_EMAIL, NEW_PASSWORD, NEW_PASSWORD, TEST_TOKEN);

		verify(mockUserApiService, times(1)).changePassword(TEST_EMAIL, ENCODED_PASSWORD, TEST_TOKEN);
	}
}
