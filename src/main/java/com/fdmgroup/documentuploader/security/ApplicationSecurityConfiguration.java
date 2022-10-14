package com.fdmgroup.documentuploader.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fdmgroup.documentuploader.security.authhandler.LoginAuthenticationSuccessHandler;
import com.fdmgroup.documentuploader.service.security.AppUserDetailsService;

/**
 * Central Configuration class for the use of Spring Security within the scope of this application.
 * 
 * @author Noah Anderson
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter{

	private final ApplicationContext context;
	private final AppUserDetailsService userDetailsService;
	private final LoginAuthenticationSuccessHandler authenticationSuccessHandler;

	@Value("${app.request-uris.login}")
	private String loginPath;

	@Autowired
	public ApplicationSecurityConfiguration(ApplicationContext context, AppUserDetailsService userDetailsService, 
			LoginAuthenticationSuccessHandler authenticationSuccessHandler) {
		this.context = context;
		this.userDetailsService = userDetailsService;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}
	
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(context.getBean(PasswordEncoder.class));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
					.antMatchers("/", loginPath, "/forgotPassword", "/resetPassword", "/changePassword", "/user/login", "/user/changePassword", "/register", "/css/*", "/js/*", "/register/confirm-account").permitAll()
					.antMatchers("/dashboard", "/editProfileInformation", "/serviceLevelInformation",
							"/settings", "/settings/**", "/account", "/account/**", "/serviceLevel").hasAuthority("ROLE_USER")
					.anyRequest().authenticated()
				.and()
				.formLogin()
					.loginPage(loginPath).permitAll()
					.loginProcessingUrl(loginPath)
					.failureUrl("/login?error=true")
					.successHandler(authenticationSuccessHandler)
					.usernameParameter("email")
					.passwordParameter("password")
				.and()
					.logout()
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl(loginPath).permitAll()
				.and()
					.rememberMe()
						.rememberMeParameter("remember-me")
						.alwaysRemember(true)
				.and()
					.sessionManagement()
					.maximumSessions(1)
					.expiredUrl(loginPath);
		}
}
