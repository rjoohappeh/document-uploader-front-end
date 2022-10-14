package com.fdmgroup.documentuploader.service.api.account;

import java.util.List;
import java.util.Optional;

import com.fdmgroup.documentuploader.enums.ApiUri;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.api.AbstractApiService;

import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link AbstractAccountApiService} which contains 
 * concrete implementations of all methods defined in both the beforementioned 
 * and {@link AbstractApiService} interfaces.
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class AccountApiService implements AbstractAccountApiService {

	private static final String DOCUMENT_NAME = "documentName";
	private static final String NAME = "name";
	private static final String QUERY_PARAM_ID = "id";
	private static final String PATH_PARAM_ID = "/{id}";
	private static final String OWNER_ID = "ownerId";
	private static final String USER_ID = "userId";

	/**
	 * Used to perform HTTP Requests and retrieve data from the associated HTTP
	 * Responses.
	 */
	private final WebClient webClient;
	
	public AccountApiService(@Value("${data.service.url}") String baseUrl) {
		this.webClient = WebClient.builder().baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	@Override
	public Account save(Account account) {
		return webClient.post()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.build())
				.bodyValue(account)
				.retrieve()
				.bodyToMono(Account.class)
				.block();
	}

	@Override
	public Optional<Account> findById(Long id) {
		return webClient.get()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.queryParam(QUERY_PARAM_ID, id)
						.build())
				.retrieve()
				.bodyToMono(Account.class)
				.blockOptional();
	}

	@Override
	public Mono<Account> update(Account account) {
		return webClient.put()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.build())
				.bodyValue(account)
				.retrieve()
				.bodyToMono(Account.class);
	}

	@Override
	public Mono<Account> deleteById(Long accountId) {
		return webClient.delete()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.queryParam(QUERY_PARAM_ID, accountId)
						.build())
				.retrieve()
				.bodyToMono(Account.class);
	}

	@Override
	public Optional<Account> findByOwnerId(long ownerId) {
		return webClient.get()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.queryParam(OWNER_ID, ownerId)
						.build())
				.retrieve()
				.bodyToMono(Account.class)
				.blockOptional();
	}

	@Override
	public Optional<Account> findByName(String name) {
		return webClient.get()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.queryParam(NAME, name)
						.build())
				.retrieve()
				.bodyToMono(Account.class)
				.blockOptional();
	}

	@Override
	public List<Account> findAccountsByUserId(long userId) {
		return webClient.get()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.queryParam(USER_ID, userId)
						.build())
				.retrieve()
				.bodyToFlux(Account.class)
				.collectList()
				.toFuture()
				.join();
	}

	@Override
	public Account addDocumentToAccountByAccountId(Document document, long accountId) {
		return webClient.put()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.path(PATH_PARAM_ID)
						.path(ApiUri.DOCUMENTS.getUri())
						.build(accountId))
				.bodyValue(document)
				.retrieve()
				.onStatus(HttpStatus.BAD_REQUEST::equals,
						ClientResponse::createException)
				.bodyToMono(Account.class)
				.block();
	}

	@Override
	public Account removeDocumentFromAccountByAccountId(String documentName, long accountId) {
		return webClient.delete()
				.uri(builder -> builder.path(ApiUri.ACCOUNTS.getUri())
						.path(PATH_PARAM_ID)
						.path(ApiUri.DOCUMENTS.getUri())
						.queryParam(DOCUMENT_NAME, documentName)
						.build(accountId))
				.retrieve()
				.onStatus(HttpStatus.BAD_REQUEST::equals,
						ClientResponse::createException)
				.bodyToMono(Account.class)
				.block();
	}
}
