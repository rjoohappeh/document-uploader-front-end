package com.fdmgroup.documentuploader.service.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fdmgroup.documentuploader.enums.ApiUri;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fdmgroup.documentuploader.model.user.AuthGroup;

/**
 * <p>
 * Service class which contains methods that submit HTTP Requests to and
 * HTTP Responses from the REST API for {@link AuthGroup} based operations.
 * </p>
 * 
 * @author Noah Anderson
 */
@Service
public class AuthGroupService {

	private static final String USERNAME = "username";
	
	/**
	 * Used to perform HTTP Requests and retrieve data from the associated HTTP Responses.
	 */
	private final WebClient webClient;
	
	public AuthGroupService(@Value("${data.service.url}") String baseUrl) {
		this.webClient = WebClient.builder()
							.baseUrl(baseUrl)
							.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.build();
	}
	
	/**
	 * Sends an HTTP Request to a REST API endpoint to find an {@link AuthGroup} object with a username equal to
	 * {@code username}.
	 * 
	 * @param username the {@code username} to search for {@link AuthGroup} objects with
	 * @return {@link List} containing the {@link AuthGroup} instances attached to the given
	 * @see AuthGroup
	 * @see Optional
	 */
	public List<AuthGroup> findByUsername(String username) {
		return webClient
					.get()
					.uri(builder -> builder
							.path(ApiUri.AUTH_GROUP.getUri())
							.queryParam(USERNAME, username)
							.build())
					.retrieve()
					.bodyToFlux(AuthGroup.class)
					.toStream()
					.collect(Collectors.toList());
	}
}
