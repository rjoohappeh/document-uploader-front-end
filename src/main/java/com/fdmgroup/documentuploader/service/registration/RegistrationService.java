package com.fdmgroup.documentuploader.service.registration;

import java.util.HashSet;
import java.util.Locale;

import com.fdmgroup.documentuploader.enums.ApiUri;
import com.fdmgroup.documentuploader.exception.PasswordsDoNotMatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fdmgroup.documentuploader.dto.register.RegisterDto;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.registration.RegistrationWrapper;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.Role;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * <p>
 * Service class which is responsible for the creation and sending of HTTP 
 * Requests and receiving of HTTP Responses to the REST API for 
 * {@link RegisterDto} based operations.
 * </p>
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class RegistrationService implements AbstractRegistrationService {
	
	/**
	 * Used to perform HTTP Requests and retrieve data from the associated HTTP Responses.
	 */
	private final WebClient webClient;
	
	/**
	 * Used to encode user passwords.
	 */
	private final PasswordEncoder passwordEncoder;

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;

	/**
	 * Provides information about the application as a whole as well 
	 * as allows simple retrieval of Spring Beans.
	 */
	private final ApplicationContext applicationContext;
	
	@Autowired
	public RegistrationService(@Value("${data.service.url}") String baseUrl, PasswordEncoder passwordEncoder,
							   MessageSource messageSource, ApplicationContext applicationContext) {
		this.webClient = WebClient.builder()
							.baseUrl(baseUrl)
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.build();
		this.passwordEncoder = passwordEncoder;
		this.messageSource = messageSource;
		this.applicationContext = applicationContext;
	}
	
	@Override
	public User registerNewAccount(RegisterDto registerDto) {
		String password = registerDto.getPassword();
		String passwordConfirmation = registerDto.getPasswordConfirmation();
		if (!password.equals(passwordConfirmation)) {
			throw new PasswordsDoNotMatchException(messageSource.getMessage("password.must-match",
					null, Locale.getDefault()));
		}

		User user = registerDto.getUser();
		user.setPassword(passwordEncoder.encode(password));

		Account account = registerDto.getAccount();
		account.setOwner(user);
		account.setDocuments(new HashSet<>());
		account.setUsers(new HashSet<>());
		
		String userEmail = user.getEmail();
		AuthGroup authGroup = applicationContext.getBean(AuthGroup.class, userEmail, Role.ROLE_USER);

		RegistrationWrapper wrapper = applicationContext.getBean(RegistrationWrapper.class, user, account, authGroup);
		processRegistration(wrapper);
		
		return user;
	}
	
	/**
	 * Sends an HTTP Request to a REST API endpoint to process a user's registration
	 * based on the information encapsulated within the given {@link RegistrationWrapper}
	 * object.
	 * 
	 * @param wrapper object encapsulating information required for registration
	 */
	private void processRegistration(RegistrationWrapper wrapper) {
		webClient
				.post()
				.uri(ApiUri.REGISTER.getUri())
				.bodyValue(wrapper)
				.retrieve()
				.toBodilessEntity()
				.subscribe();
	}
	
	/**
	 * Sends an HTTP Request to a REST API endpoint to confirm a {@link User} account with 
	 * the registration token sent to the email used to register the user.
	 * 
	 * @param confirmationToken the token String sent to the email of a {@code User} upon
	 * 		  successful account registration
	 * @return {@code true} if confirmation is successful, {@code false} otherwise
	 */
	public Boolean confirmRegistration(String confirmationToken) {
		String path = ApiUri.REGISTER.getUri() + ApiUri.CONFIRM_TOKEN.getUri();
		return webClient
					.patch()
					.uri(path)
					.bodyValue(confirmationToken)
					.retrieve()
					.bodyToMono(Boolean.class)
					.toFuture()
					.join();
	}
}
