package com.fdmgroup.documentuploader.controller;

import java.util.List;

import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.security.AppUserPrincipal;
import com.fdmgroup.documentuploader.service.account.AccountService;

@Controller
@SessionAttributes({ "user" })
@RequestMapping("${app.request-uris.dashboard}")
@PreAuthorize("isAuthenticated()")
public class DashboardController {

	private final AccountService accountService;
	
	@Autowired
	public DashboardController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping
	public String returnToDashboard(Authentication authentication, Model model) {
		AppUserPrincipal userDetails = (AppUserPrincipal) authentication.getPrincipal();
		User user = userDetails.getUser();

		List<Account> accounts = accountService.getAllAccountsByUserId(user.getId());
		model.addAttribute(AttributeName.ACCOUNTS.getValue(), accounts);
		
		return ViewPath.DASHBOARD.getPath();
	}

}
