package com.fdmgroup.documentuploader.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.dto.resetPassword.ResetPasswordDto;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
class ResetPasswordControllerTest {

    private static final String VALID_EMAIL = "validEmail@email.com";
    private static final String VALID_TOKEN = "ValidToken123";
    private static final String TOKEN_QUERY_PARAM = "token";

    private static ObjectMapper objectMapper;

    private ResetPasswordDto invalidResetPasswordDto;
    private ResetPasswordDto validResetPasswordDto;

    private HttpHeaders headers;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    private ApplicationProperties.RequestUris requestUris;


    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setup(@Autowired ApplicationProperties applicationProperties) {
        this.requestUris = applicationProperties.getRequestUris();
        this.invalidResetPasswordDto = new ResetPasswordDto();
        this.validResetPasswordDto = new ResetPasswordDto();
        this.headers = getTestHeaders();
        validResetPasswordDto.setEmail(VALID_EMAIL);
        validResetPasswordDto.setNewPassword("newPassword");
        validResetPasswordDto.setNewPasswordConfirmation("newPassword");
    }

    private HttpHeaders getTestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("host", "localhost:8088");
        headers.add("referer", "http://localhost:8088/forgotPassword");

        return headers;
    }

    @Test
    void testForgotPassword_returnsViewWithNameOfForgotPassword() throws Exception {
        mockMvc.perform(get(requestUris.getForgotPassword()))
                .andExpect(view().name("forgotPassword"));
    }

    @Test
    void testResetPassword_respondsCorrectlyWhenInvalidEmailIsGiven() throws Exception {
        mockMvc.perform(post(requestUris.getResetPassword())
                .headers(headers)
                .queryParam(AttributeName.EMAIL.getValue(), " "))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(matchAll(
                        status().is3xxRedirection(),
                        flash().attribute(AttributeName.MESSAGE.getValue(), "resetPassword.userEmail: Email must be in a valid email format."),
                        redirectedUrl(requestUris.getForgotPassword())
                ))
                .andDo(print());
    }

    @Test
    void testResetPassword_callsUserServiceProcessResetPasswordRequest_andRespondsCorrectly_whenValidEmailIsGiven() throws Exception {
        mockMvc.perform(post(requestUris.getResetPassword())
                .queryParam(AttributeName.EMAIL.getValue(), VALID_EMAIL))
                .andExpect(result -> verify(mockUserService, times(1)).processResetPasswordRequest(VALID_EMAIL))
                .andExpect(matchAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/login"),
                        flash().attribute(AttributeName.MESSAGE.getValue(), "Please click the link in the email sent to you to reset your password.")
                ))
                .andDo(print());
    }

    @Test
    void testChangePassword_respondsCorrectlyWhenEmptyTokenStringIsGiven() throws Exception {
        mockMvc.perform(get(requestUris.getChangePassword())
                .queryParam(TOKEN_QUERY_PARAM, Strings.EMPTY)
                .headers(headers))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(matchAll(
                        status().is3xxRedirection(),
                        redirectedUrl(requestUris.getForgotPassword()),
                        flash().attribute(AttributeName.MESSAGE.getValue(), "changePassword.passwordResetToken: A Token must be provided.")
                ))
                .andDo(print());
    }

    @Test
    void testChangePassword_callsUserServiceIsValidPasswordResetToken_andRespondsCorrectlyBasedOnTheValueReturned() throws Exception {
        when(mockUserService.isValidPasswordResetToken(VALID_TOKEN)).thenReturn(true);

        mockMvc.perform(get(requestUris.getChangePassword())
                .queryParam(TOKEN_QUERY_PARAM, VALID_TOKEN)
                .headers(headers))
                .andExpect(matchAll(
                        status().isOk(),
                        view().name(ViewPath.CHANGE_PASSWORD.getPath())
                ));
        verify(mockUserService, times(1)).isValidPasswordResetToken(VALID_TOKEN);
    }

    @Test
    void testSubmitChangePassword_respondsCorrectlyWhenInvalidParametersAreGiven() throws Exception {
        mockMvc.perform(post(requestUris.getChangePassword())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResetPasswordDto))
                .headers(headers))
                .andExpect(matchAll(
                        status().is3xxRedirection(),
                        redirectedUrl(requestUris.getForgotPassword())
                ))
                .andDo(print());
    }

    @Test
    void testSubmitChangePassword_throwsPasswordsDoNotMatchException_whenThrownByUserService() throws Exception {
        doThrow(new PasswordsDoNotMatchException("New password must match confirmation."))
				.when(mockUserService)
				.resetPassword(validResetPasswordDto.getEmail(), validResetPasswordDto.getNewPassword(), validResetPasswordDto.getNewPasswordConfirmation(), VALID_TOKEN);

		mockMvc.perform(post(requestUris.getChangePassword())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validResetPasswordDto))
				.headers(headers))
				.andExpect(matchAll(
						status().is3xxRedirection(),
						redirectedUrl(requestUris.getForgotPassword())
				))
				.andDo(print());
    }
}
