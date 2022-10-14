package com.fdmgroup.documentuploader.controller;

import javax.servlet.http.Cookie;

import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.dto.login.LoginDto;

@Controller
public class LoginController {

	private static final String REDIRECT = "redirect:";
	private final RequestUris requestUris;

	@Autowired
	public LoginController(ApplicationProperties applicationProperties) {
		super();
		this.requestUris = applicationProperties.getRequestUris();
	}

	@ModelAttribute
	public void addAttributes(Model model, @Autowired LoginDto loginDto) {
		model.addAttribute(AttributeName.LOGIN_DTO.getValue(), loginDto);
	}

	@GetMapping({ "/", "/login", "/user/login" })
	public String toLogin(@CookieValue(value = "remember-me", required = false) Cookie rememberMe) {
		if (rememberMe != null) {
			return REDIRECT + requestUris.getDashboard();
		}
		return ViewPath.LOGIN.getPath();
	}

}
