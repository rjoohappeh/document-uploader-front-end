package com.fdmgroup.documentuploader.controller;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.AccountCostCalculator;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.account.AbstractAccountService;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import com.fdmgroup.documentuploader.service.user.UserService;

@Controller
@SessionAttributes({ "user", "account" })
@RequestMapping("${app.request-uris.settings}")
@PreAuthorize("isAuthenticated()")
public class SettingsController {
	
	private static final String REDIRECT = "redirect:";
	
	private final AbstractUserService userService;
	private final AccountCostCalculator costCalculator;
	private final AbstractAccountService accountService;
	private final RequestUris requestUris;
	private final MessageSource messageSource;
	
	@Autowired
	public SettingsController(AccountCostCalculator costCalculator, UserService userService,
			AbstractAccountService accountService, ApplicationProperties applicationProperties,
			MessageSource messageSource) {
		this.costCalculator = costCalculator;
		this.userService = userService;
		this.accountService = accountService;
		this.requestUris = applicationProperties.getRequestUris();
		this.messageSource = messageSource;
	}

	@ModelAttribute
	public void addAttributesToModel(Model model, User user) {
		Optional<Account> optionalAccount = accountService.findByOwnerId(user.getId());
		if (optionalAccount.isPresent()) {
			Account account = optionalAccount.get();
			model.addAttribute(AttributeName.ACCOUNT.getValue(), account);

			BigDecimal price = costCalculator.calculateRate(account);
			model.addAttribute(AttributeName.PRICE.getValue(), price);

			model.addAttribute(AttributeName.LEVELS.getValue(), getServiceLevels(account));
		}
	}

	private EnumSet<ServiceLevel> getServiceLevels(Account account) {
		EnumSet<ServiceLevel> levels = EnumSet.allOf(ServiceLevel.class);
		levels.remove(account.getServiceLevel());

		return levels;
	}

	@GetMapping
	public String toSettings() {
		return ViewPath.SETTINGS.getPath();
	}

	@GetMapping("${app.request-uris.edit-profile-info}")
	public String toEditProfileInformation() {
		return ViewPath.EDIT_PROFILE_INFO.getPath();
	}

	@GetMapping("${app.request-uris.service-level}")
	public String toServiceLevel() {
		return ViewPath.SERVICE_LEVEL_INFO.getPath();
	}
	
	@PostMapping("${app.request-uris.update-password}")
	public String updatePassword(User user, RedirectAttributes redirectAttributes,
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("newPasswordConfirmation") String newPasswordConfirmation) {
		userService.updatePassword(user, currentPassword, newPassword, newPasswordConfirmation);
		
		String message = messageSource.getMessage("user.password-changed", null, Locale.getDefault());
		redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(), message);
		
		return REDIRECT + requestUris.getSettings() + requestUris.getEditProfileInfo();
	}

	@PostMapping("${app.request-uris.upgrade-service-level}")
	public String editServiceLevel(Account account, HttpSession httpSession,
			@RequestParam(name = "selected") String serviceLevelName) {
		Account updatedAccount = accountService.updateAccountServiceLevel(account, serviceLevelName);
		updateAccountInSession(httpSession, updatedAccount);
		
		return REDIRECT + requestUris.getSettings() + requestUris.getServiceLevel();
	}

	private void updateAccountInSession(HttpSession httpSession, Account account) {
		httpSession.setAttribute(AttributeName.ACCOUNT.getValue(), account);
	}

	@PostMapping("${app.request-uris.add-user}")
	public String addUser(Account account, HttpSession httpSession,
			@RequestParam String guestEmail) {
		Account updatedAccount = accountService.addUserToAccountByEmail(guestEmail, account);
		updateAccountInSession(httpSession, updatedAccount);

		return REDIRECT + requestUris.getSettings() + requestUris.getServiceLevel();
	}

	@GetMapping("${app.request-uris.delete-user}" + "/{deletedId}")
	public String deleteUser(User user, Account account, HttpSession httpSession,
			@PathVariable long deletedId) {
		Account updatedAccount = accountService.removeUserFromAccountById(deletedId, user, account);
		updateAccountInSession(httpSession, updatedAccount);

		return REDIRECT + requestUris.getSettings() + requestUris.getServiceLevel();
	}

}
