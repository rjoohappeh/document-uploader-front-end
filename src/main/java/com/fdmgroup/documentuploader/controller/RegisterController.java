package com.fdmgroup.documentuploader.controller;

import com.fdmgroup.documentuploader.dto.register.RegisterDto;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.EnumSet;
import java.util.Locale;

@Controller
@RequestMapping("/register")
public class RegisterController {

	private static final String REDIRECT = "redirect:";

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;
	private final ApplicationContext ctx;
	private final RegistrationService registrationService;

	@Autowired
	public RegisterController(MessageSource messageSource, ApplicationContext ctx,
			RegistrationService registrationService) {
		super();
		this.messageSource = messageSource;
		this.ctx = ctx;
		this.registrationService = registrationService;
	}

	@ModelAttribute
	public void addAttributes(Model model) {
		model.addAttribute(AttributeName.LEVELS.getValue(), EnumSet.allOf(ServiceLevel.class));
	}

	@GetMapping
	public String toRegister(Model model) {
		String registerDtoAttribute = AttributeName.REGISTER_DTO.getValue();
		model.addAttribute(registerDtoAttribute, ctx.getBean(registerDtoAttribute, RegisterDto.class));

		return ViewPath.REGISTRATION.getPath();
	}

	@PostMapping
	public String afterRegister(@Valid RegisterDto registerDto, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return ViewPath.REGISTRATION.getPath();
		}

		User user = registrationService.registerNewAccount(registerDto);
		String userEmail = user.getEmail();

		redirectAttributes.addFlashAttribute(AttributeName.EMAIL.getValue(), userEmail);
		redirectAttributes.addFlashAttribute(AttributeName.REGISTER_SUCCESS.getValue(),
					messageSource.getMessage("registration.verification-email", null, Locale.getDefault()) + userEmail);

		return REDIRECT + "/" + ViewPath.LOGIN.getPath();
	}

	@GetMapping("${app.request-uris.confirm-account}")
	public String confirmRegistration(RedirectAttributes redirectAttributes, @RequestParam("token") String confirmationToken) {
		boolean success = registrationService.confirmRegistration(confirmationToken);

		if (success) {
			redirectAttributes.addFlashAttribute(AttributeName.CONFIRM_TOKEN_SUCCESS.getValue(),
					messageSource.getMessage("registration.completed", null, Locale.getDefault()));
		} else {
			redirectAttributes.addFlashAttribute(AttributeName.CONFIRM_TOKEN_FAILURE.getValue(),
					messageSource.getMessage("registration.token.confirmation-failed", null, Locale.getDefault()));
		}

		return REDIRECT + "/" + ViewPath.LOGIN.getPath();
	}
}
