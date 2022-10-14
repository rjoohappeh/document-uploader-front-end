package com.fdmgroup.documentuploader.service.api.user;

import java.util.Optional;

import com.fdmgroup.documentuploader.enums.ApiUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.AbstractApiService;

import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link AbstractUserApiService} which contains 
 * concrete implementations of all methods defined in both the beforementioned 
 * and {@link AbstractApiService} interfaces.
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class UserApiService implements AbstractUserApiService {

	private static final String QUERY_PARAM_ID = "id";
	private static final String QUERY_PARAM_EMAIL = "email";
	private static final String QUERY_PARAM_NEW_PASSWORD = "newPassword";
	private static final String QUERY_PARAM_TOKEN = "token";
	private static final String PATH_PARAM_EMAIL = "/{email}";
	
	/**
	 * Used to perform HTTP Requests and retrieve data from the associated HTTP
	 * Responses.
	 */
	private final WebClient webClient;
	
	@Autowired
	public UserApiService(@Value("${data.service.url}") String baseUrl) {
		super();
		this.webClient = WebClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	@Override
	public User save(User user) {
		return webClient.post().uri(builder -> builder.path(ApiUri.USERS.getUri())
				.build())
			.retrieve()
				.bodyToMono(User.class)
				.block();
	}

	@Override
	public Optional<User> findById(Long id) {
		return webClient.get().uri(builder -> builder.path(ApiUri.USERS.getUri())
				.queryParam(QUERY_PARAM_ID, id)
				.build())
			.retrieve()
				.bodyToMono(User.class)
				.blockOptional();
	}

	@Override
	public Mono<User> update(User user) {
		return webClient.put().uri(builder -> builder.path(ApiUri.USERS.getUri())
				.build())
				.bodyValue(user)
			.retrieve()
				.onStatus(HttpStatus.NOT_FOUND::equals,
						ClientResponse::createException)
				.bodyToMono(User.class);
	}

	@Override
	public Mono<User> deleteById(Long id) {
		return webClient.delete().uri(builder -> builder.path(ApiUri.USERS.getUri())
				.queryParam(QUERY_PARAM_ID, id)
				.build())
			.retrieve()
				.bodyToMono(User.class);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return webClient.get().uri(builder -> builder.path(ApiUri.USERS.getUri())
				.queryParam(QUERY_PARAM_EMAIL, email)
				.build(email))
			.retrieve()
				.bodyToMono(User.class)
				.blockOptional();
	}

	@Override
	public boolean isEnabled(String email) {
		String path = ApiUri.USERS.getUri() + PATH_PARAM_EMAIL + ApiUri.IS_ENABLED.getUri();
		return webClient.get().uri(builder -> builder.path(path)
				.build(email))
			.retrieve()
				.onStatus(HttpStatus.NOT_FOUND::equals,
						ClientResponse::createException)
				.bodyToMono(Boolean.class)
				.toFuture()
				.join();
	}

	@Override
	public void resetPassword(String email) {
		String path = ApiUri.USERS.getUri() + PATH_PARAM_EMAIL + ApiUri.RESET_PASSWORD.getUri();
		webClient.post().uri(builder -> builder.path(path)
				.build(email))
			.retrieve()
				.onStatus(HttpStatus.NOT_FOUND::equals,
						ClientResponse::createException)
				.toBodilessEntity()
				.subscribe();
	}

	@Override
	public boolean isValidPasswordResetToken(String passwordResetToken) {
		String path = ApiUri.USERS.getUri() + ApiUri.RESET_PASSWORD.getUri() + ApiUri.TOKEN.getUri();
		return webClient.get().uri(builder -> builder.path(path)
				.queryParam(QUERY_PARAM_TOKEN, passwordResetToken)
				.build())
			.retrieve()
				.bodyToMono(Boolean.class)
				.block();
	}

	@Override
	public void changePassword(String email, String newPassword, String passwordResetToken) {
		String path = ApiUri.USERS.getUri() + ApiUri.RESET_PASSWORD.getUri();
		webClient.post().uri(builder -> builder.path(path)
				.queryParam(QUERY_PARAM_EMAIL, email)
				.queryParam(QUERY_PARAM_NEW_PASSWORD, newPassword)
				.queryParam(QUERY_PARAM_TOKEN, passwordResetToken)
				.build())
			.exchange()
				.flatMap(clientResponse -> {
					if (clientResponse.statusCode() != null && clientResponse.statusCode().is4xxClientError()) {
						return clientResponse.createException().flatMap(Mono :: error);
					}
					return clientResponse.toBodilessEntity();
				})
				.block();
	}
}
