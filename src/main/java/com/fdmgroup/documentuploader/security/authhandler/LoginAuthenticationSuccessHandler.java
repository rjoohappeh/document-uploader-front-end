package com.fdmgroup.documentuploader.security.authhandler;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fdmgroup.documentuploader.enums.AttributeName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.security.AppUserPrincipal;

/**
 * <p>
 * Custom implementation of the {@link AuthenticationSuccessHandler} interface.
 * </p>
 * <p>
 * The
 * {@link #onAuthenticationSuccess(HttpServletRequest, HttpServletResponse, Authentication)
 * authenticationSuccess()} method implementation is invoked when a user has
 * been successfully authenticated within the system.
 * </p>
 * 
 * @author Noah Anderson
 *
 */
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final String NONE = "none";
	private final RequestUris requestUris;

	@Autowired
	public LoginAuthenticationSuccessHandler(ApplicationProperties applicationProperties) {
		super();
		this.requestUris = applicationProperties.getRequestUris();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		HttpSession session = request.getSession();

		String roleAttribute = AttributeName.ROLE.getValue();
		Object principal = authentication.getPrincipal();
		if (principal instanceof Principal) {
			session.setAttribute(roleAttribute, NONE);
		} else {
			AppUserPrincipal userPrincipal = (AppUserPrincipal) principal;
			session.setAttribute(roleAttribute, String.valueOf(userPrincipal.getAuthorities()));

			User user = userPrincipal.getUser();
			session.setAttribute(AttributeName.USER.getValue(), user);
		}
		response.sendRedirect(requestUris.getDashboard());
	}

}
