package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.dto.register.RegisterDto;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.registration.RegistrationService;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

	private static final String TEST = "test";
	private static final String TEST_EMAIL = "test@email.com";
	private static final String TEST_PASSWORD = "!1Qqazse4";
	private static final String TEST_SERVICE_LEVEL = "BRONZE";
	private static final String REGISTER_DTO_ATTR_NAME = "registerDto";
	private static final String TOKEN = "token";

	private static RegisterDto validRegisterDto;
	private RegisterDto invalidRegisterDto;

	@MockBean
	private RegistrationService mockRegistrationService;
	@MockBean
	private PasswordEncoder mockPasswordEncoder;
	@MockBean
	private UserService mockUserService;

	@Mock
	private User mockUser;
	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	static void init() throws Exception {
		validRegisterDto = new RegisterDto();
		validRegisterDto.setFirstName(TEST);
		validRegisterDto.setLastName(TEST);
		validRegisterDto.setEmail(TEST_EMAIL);
		validRegisterDto.setPassword(TEST_PASSWORD);
		validRegisterDto.setPasswordConfirmation(TEST_PASSWORD);
		validRegisterDto.setAccountName(TEST);
		validRegisterDto.setServiceLevel(TEST_SERVICE_LEVEL);
	}

	@BeforeEach
	void setup() throws Exception {
		this.invalidRegisterDto = new RegisterDto();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testToRegister_addsRegisterDtoObjectToModelAndReturnsViewNamedRegistration() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(matchAll(
						model().attribute("registerDto", invalidRegisterDto),
						view().name("registration")))
				.andDo(print());
	}

	@Test
	void testAfterRegister_returnsModelWithErrors_whenInvalidRegisterDtoIsGiven() throws Exception {
		// the registerDto is invalid because the properties have not been set to valid values

		// this must be set because the RegisterDto.getAccount() uses "this.serviceLevel.toUpperCase()"
		// if this is not set a NullPointerException is thrown
		this.invalidRegisterDto.setServiceLevel(TEST_SERVICE_LEVEL);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.flashAttr(REGISTER_DTO_ATTR_NAME, invalidRegisterDto))
				.andExpect(matchAll(
						status().isOk(),
						model().hasErrors(),
						view().name("registration")))
				.andDo(print());
	}

	@Test
	void testAfterRegister_returnsIsFoundStatusAndRedirectsToLogin_whenValidRegisterDtoIsGiven() throws Exception {
		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.flashAttr(REGISTER_DTO_ATTR_NAME, validRegisterDto))
				.andExpect(matchAll(
						status().isFound(),
						redirectedUrl("/login")))
				.andDo(print());
	}

	@Test
	void testAfterRegister_callsRegistrationServiceRegisterNewAccount() throws Exception {
		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.flashAttr(REGISTER_DTO_ATTR_NAME, validRegisterDto))
				.andDo(print());

		verify(mockRegistrationService).registerNewAccount(validRegisterDto);
	}

	@Test
	void testAfterRegister_callsUserGetEmail() throws Exception {
		when(mockRegistrationService.registerNewAccount(validRegisterDto)).thenReturn(mockUser);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.flashAttr(REGISTER_DTO_ATTR_NAME, validRegisterDto))
				.andDo(print());

		verify(mockUser).getEmail();
	}

	@Test
	void testAfterRegister_addsEmailAndRegisterSuccessAttributesToFlashMap() throws Exception {
		when(mockRegistrationService.registerNewAccount(validRegisterDto)).thenReturn(mockUser);
		when(mockUser.getEmail()).thenReturn(TEST_EMAIL);

		mockMvc.perform(post("/register")
				.contentType(MediaType.APPLICATION_JSON)
				.flashAttr(REGISTER_DTO_ATTR_NAME, validRegisterDto))
				.andExpect(matchAll(
						flash().attribute("email", TEST_EMAIL),
						flash().attribute("registerSuccess", "A verification email has been sent to : "
								+ TEST_EMAIL)))
				.andDo(print());
	}

	@Test
	void testConfirmRegistration_callsRegistrationServiceConfirmRegistration() throws Exception {
		mockMvc.perform(get("/register/confirm-account")
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam(TOKEN, TOKEN))
				.andDo(print());

		verify(mockRegistrationService).confirmRegistration(TOKEN);
	}

	@Test
	void testConfirmRegistration_returnsIsFoundStatusAndRedirectsToLogin() throws Exception {
		mockMvc.perform(get("/register/confirm-account")
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam(TOKEN, TOKEN))
				.andExpect(matchAll(
						status().isFound(),
						redirectedUrl("/login")))
				.andDo(print());
	}

	@Test
	void testConfirmRegistration_addsConfirmTokenSuccessAttributeToFlashMap_whenTokenConfirmationIsSuccessful() throws Exception {
		when(mockRegistrationService.confirmRegistration(TOKEN)).thenReturn(true);

		mockMvc.perform(get("/register/confirm-account")
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam(TOKEN, TOKEN))
				.andExpect(
						flash().attribute("confirmTokenSuccess", "Account registration successfully completed!"))
				.andDo(print());
	}

	@Test
	void testConfirmRegistration_addsConfirmTokenSuccessAttributeToFlashMap_whenTokenConfirmationIsNotSuccessful() throws Exception {
		mockMvc.perform(get("/register/confirm-account")
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam(TOKEN, TOKEN))
				.andExpect(
						flash().attribute("confirmTokenFailure", "Invalid or Expired token used"))
				.andDo(print());
	}
}
