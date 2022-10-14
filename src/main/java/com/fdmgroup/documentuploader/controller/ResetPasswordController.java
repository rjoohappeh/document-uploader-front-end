package com.fdmgroup.documentuploader.controller;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fdmgroup.documentuploader.dto.resetPassword.ResetPasswordDto;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;

import java.util.Locale;

@Controller
@Validated
public class ResetPasswordController {

	private static final String REDIRECT = "redirect:";

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;
	private final AbstractUserService userService;
	private final RequestUris requestUris;

	@Autowired
	public ResetPasswordController(MessageSource messageSource, AbstractUserService userService, ApplicationProperties applicationProperties) {
		super();
		this.messageSource = messageSource;
		this.userService = userService;
		this.requestUris = applicationProperties.getRequestUris();
	}

	@GetMapping("${app.request-uris.forgot-password}")
	public String forgotPassword() {
		return ViewPath.FORGOT_PASSWORD.getPath();
	}

	@PostMapping("${app.request-uris.reset-password}")
	public String resetPassword(
			@Email(message = "{email.matches-pattern}") @RequestParam(value = "email") String userEmail,
			RedirectAttributes redirectAttributes) {
		userService.processResetPasswordRequest(userEmail);
		redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(),
				messageSource.getMessage("token.check-email", null, Locale.getDefault()));
		return REDIRECT + requestUris.getLogin();
	}

	@GetMapping("${app.request-uris.change-password}")
	public String changePassword(
			@NotBlank(message = "{token.not-empty}") @RequestParam(value = "token") String passwordResetToken,
			RedirectAttributes redirectAttributes, Model model,
			@Autowired @Qualifier(value = "resetPasswordDto") ResetPasswordDto resetPasswordDto) {
		boolean isValidToken = userService.isValidPasswordResetToken(passwordResetToken);
		if (!isValidToken) {
			redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(),
					messageSource.getMessage("token.is-invalid", null, Locale.getDefault()));
			return REDIRECT + requestUris.getLogin();
		}
		model.addAttribute(AttributeName.TOKEN.getValue(), passwordResetToken);
		model.addAttribute(AttributeName.RESET_PASSWORD_DTO.getValue(), resetPasswordDto);
		return ViewPath.CHANGE_PASSWORD.getPath();
	}

	@PostMapping("${app.request-uris.change-password}")
	public String submitChangePassword(@Valid ResetPasswordDto resetPasswordDto,
			@NotBlank(message = "token.not-empty") @RequestParam(value = "token") String passwordResetToken,
			RedirectAttributes redirectAttributes) {
		userService.resetPassword(resetPasswordDto.getEmail(), resetPasswordDto.getNewPassword(),
				resetPasswordDto.getNewPasswordConfirmation(), passwordResetToken);
		redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(),
				messageSource.getMessage("password.is-reset", null, Locale.getDefault()));
		return REDIRECT + requestUris.getLogin();
	}
}
