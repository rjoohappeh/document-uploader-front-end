package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import com.fdmgroup.documentuploader.exception.CannotAddUserToAccountException;
import com.fdmgroup.documentuploader.exception.CannotRemoveUserFromAccountException;
import com.fdmgroup.documentuploader.exception.InvalidServiceLevelException;
import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.AccountCostCalculator;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.account.AccountService;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@EnableConfigurationProperties(value = ApplicationProperties.class)
@TestPropertySource(value = { "classpath:/paths.properties" })
class SettingsControllerTest {

	private static final String USER_NOT_FOUND = "User not found!";
	private static final String GUEST_EMAIL = "guestEmail";
	private static final String EMAIL = "email";
	private static final String SLASH = "/";
	private static final String NEW_PASSWORDS_DONT_MATCH = "New password must match confirmation.";
	private static final String CURRENT_PASSWORD = "currentPassword";
	private static final String NEW_PASSWORD_CONFIRMATION = "newPasswordConfirmation";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String PASSWORD = "password";

	@Autowired
	private ApplicationProperties applicationProperties;
	private RequestUris requestUris;
	
	@Mock
	private Account mockAccount;
	@Mock
	private User mockUser;
	@MockBean
	private UserService mockUserService;
	@MockBean
	private AccountService mockAccountService;
	@MockBean
	private PasswordEncoder mockPasswordEncoder;
	@MockBean
	private AccountCostCalculator mockAccountCostCalculator;
	
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.requestUris = applicationProperties.getRequestUris();
	}

	@Test
	@WithMockUser(roles = "USER")
	void testToSettings_respondsWithViewNamedSettings() throws Exception {
		when(mockUser.getId()).thenReturn(0L);
		
		mockMvc.perform(get(requestUris.getSettings())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
				.andExpect(matchAll(
						status().isOk(),
						view().name(ViewPath.SETTINGS.getPath())));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testToEditProfileInformationRespondsWithViewNamed_usersSlashEditProfileInformation() throws Exception {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.BRONZE);
		
		mockMvc.perform(get(requestUris.getSettings() + requestUris.getEditProfileInfo())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
				.andExpect(matchAll(
						status().isOk(),
						view().name(ViewPath.EDIT_PROFILE_INFO.getPath())));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testUpdatePassword_throwsPasswordsDoNotMatchException_whenNewPassword_doesNotMatch_newPasswordConfirmation() throws Exception {
		when(mockUserService.updatePassword(mockUser, CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD_CONFIRMATION)).thenThrow(new PasswordsDoNotMatchException(NEW_PASSWORDS_DONT_MATCH));
		
		MvcResult result = mockMvc.perform(post(requestUris.getSettings() + requestUris.getUpdatePassword())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param(CURRENT_PASSWORD, CURRENT_PASSWORD)
				.param(NEW_PASSWORD, NEW_PASSWORD)
				.param(NEW_PASSWORD_CONFIRMATION, NEW_PASSWORD_CONFIRMATION))
				.andExpect(matchAll(
					status().is3xxRedirection(),
					redirectedUrl(requestUris.getLogin())
				))
				.andReturn();
		
		assertTrue(result.getResolvedException() instanceof PasswordsDoNotMatchException);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testUpdatePasswordCallsUserService_updateUser_whenEnteredPasswordMatchesUserPassword() throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(PASSWORD);
		when(mockUser.getPassword()).thenReturn(encodedPassword);
		when(mockPasswordEncoder.matches(PASSWORD, encodedPassword)).thenReturn(true);
		
		mockMvc.perform(post(requestUris.getSettings() + requestUris.getUpdatePassword())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param(CURRENT_PASSWORD, PASSWORD)
				.param(NEW_PASSWORD, NEW_PASSWORD)
				.param(NEW_PASSWORD_CONFIRMATION, NEW_PASSWORD))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getSettings() + requestUris.getEditProfileInfo())
				));
		
		verify(mockUserService).updatePassword(mockUser, PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testUpdatePasswordAdds_message_toRedirectAttributes_whenNoExceptionIsThrown() throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(PASSWORD);
		when(mockUser.getPassword()).thenReturn(encodedPassword);
		when(mockPasswordEncoder.matches(PASSWORD, encodedPassword)).thenReturn(true);

		mockMvc.perform(post(requestUris.getSettings() + requestUris.getUpdatePassword())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param(CURRENT_PASSWORD, PASSWORD)
				.param(NEW_PASSWORD, NEW_PASSWORD)
				.param(NEW_PASSWORD_CONFIRMATION, NEW_PASSWORD))
				.andExpect(matchAll(
						flash().attributeExists(AttributeName.MESSAGE.getValue())
				));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testToServiceLevel_returnsLoginView_whenNoAccountIsFoundGivenOwnerId() throws Exception {
		when(mockAccountService.findByOwnerId(anyLong())).thenReturn(Optional.empty());
		
		mockMvc.perform(get(requestUris.getSettings() + requestUris.getServiceLevel())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
				.andExpect(matchAll(
						status().isOk(),
						view().name(ViewPath.SERVICE_LEVEL_INFO.getPath())));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testToServiceLevel_addsToModel_andReturnsUsersSlashServiceLevelInformation_whenAccountIsFoundGivenOwnerId() throws Exception {
		ServiceLevel level = ServiceLevel.BRONZE;
		Set<User> users = Collections.emptySet();
		BigDecimal zero = BigDecimal.ZERO;
		
		when(mockAccountService.findByOwnerId(anyLong())).thenReturn(Optional.of(mockAccount));
		when(mockAccount.getServiceLevel()).thenReturn(level);
		when(mockAccount.getUsers()).thenReturn(users);
		when(mockAccountCostCalculator.calculateRate(mockAccount)).thenReturn(zero);
		
		EnumSet<ServiceLevel> expectedLevels = EnumSet.allOf(ServiceLevel.class);
		expectedLevels.remove(level);
		
		mockMvc.perform(get(requestUris.getSettings() + requestUris.getServiceLevel())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
				.andExpect(matchAll(
						status().isOk(),
						model().attribute(AttributeName.PRICE.getValue(), zero),
						model().attribute(AttributeName.LEVELS.getValue(), expectedLevels),
						view().name(ViewPath.SERVICE_LEVEL_INFO.getPath())));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testEditServiceLevel_throwsInvalidServiceLevelException_whenInvalidServiceLevelIsSelected() throws Exception {
		when(mockAccountService.updateAccountServiceLevel(mockAccount, Strings.EMPTY)).thenThrow(new InvalidServiceLevelException());

		MvcResult result = mockMvc.perform(post(requestUris.getSettings() + requestUris.getUpgradeServiceLevel())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param("selected", Strings.EMPTY))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getLogin())
				))
				.andReturn();
		
		assertTrue(result.getResolvedException() instanceof InvalidServiceLevelException);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testEditServiceLevel_addsToModel_andReturnsUsersSlashServiceLevelInformation_whenAccountIsFoundGivenOwnerId() throws Exception {
		ServiceLevel level = ServiceLevel.BRONZE;
		Set<User> users = Collections.emptySet();
		BigDecimal zero = BigDecimal.ZERO;
		
		when(mockAccount.getServiceLevel()).thenReturn(level);
		when(mockAccount.getUsers()).thenReturn(users);
		when(mockAccountCostCalculator.calculateRate(mockAccount)).thenReturn(zero);
		
		EnumSet<ServiceLevel> expectedLevels = EnumSet.allOf(ServiceLevel.class);
		expectedLevels.remove(level);
		
		mockMvc.perform(post(requestUris.getSettings() + requestUris.getUpgradeServiceLevel())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param("selected", "bronze"))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getSettings() + requestUris.getServiceLevel())));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testAddUser_throwsCannotAddUserToAccountException_when_accountService_addUserToAccountByEmail_throwsIt() throws Exception {
		when(mockAccountService.addUserToAccountByEmail(EMAIL, mockAccount)).thenThrow(new CannotAddUserToAccountException(USER_NOT_FOUND));
		
		MvcResult result = mockMvc.perform(post(requestUris.getSettings() + requestUris.getAddUser())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param(GUEST_EMAIL, EMAIL))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getLogin())
				))
				.andReturn();
		
		assertTrue(result.getResolvedException() instanceof CannotAddUserToAccountException);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testAddUser_callsAccountServiceUpdateAccount_whenUserIsFound() throws Exception {
		when(mockUserService.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(mockAccount.hasMaxUsers()).thenReturn(false);
		when(mockUser.getEmail()).thenReturn(EMAIL);
		
		mockMvc.perform(post(requestUris.getSettings() + requestUris.getAddUser())
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount)
				.param(GUEST_EMAIL, Strings.EMPTY))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getSettings() + requestUris.getServiceLevel())));
		
		verify(mockAccountService, times(1)).addUserToAccountByEmail(Strings.EMPTY, mockAccount);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testDeleteUser_addsUserNotFound_toModel_whenIdPathVariableDoesNotExist() throws Exception {
		when(mockAccountService.removeUserFromAccountById(0, mockUser, mockAccount)).thenThrow(new CannotRemoveUserFromAccountException());
		
		MvcResult result = mockMvc.perform(get(requestUris.getSettings() + requestUris.getDeleteUser() + SLASH + 0)
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl("/login")
				))
				.andReturn();
		
		assertTrue(result.getResolvedException() instanceof CannotRemoveUserFromAccountException);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testDeleteUser_returnsServiceLevelInfoPath_addsAttributesToModel_andCallsMockMethods_whenAccountIsFoundGivenOwnerId() throws Exception {
		ServiceLevel level = ServiceLevel.BRONZE;
		Set<User> users = Collections.emptySet();
		BigDecimal zero = BigDecimal.ZERO;
		
		when(mockUserService.findById(anyLong())).thenReturn(Optional.of(mockUser));
		when(mockAccount.getServiceLevel()).thenReturn(level);
		when(mockAccount.getUsers()).thenReturn(users);
		when(mockAccountCostCalculator.calculateRate(mockAccount)).thenReturn(zero);
		
		
		EnumSet<ServiceLevel> expectedLevels = EnumSet.allOf(ServiceLevel.class);
		expectedLevels.remove(level);
		
		mockMvc.perform(get(requestUris.getSettings() + requestUris.getDeleteUser() + SLASH + 1)
				.sessionAttr(AttributeName.USER.getValue(), mockUser)
				.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getSettings() + requestUris.getServiceLevel())
				));
		
		verify(mockAccountService, times(1)).removeUserFromAccountById(1L, mockUser, mockAccount);
	}
}
