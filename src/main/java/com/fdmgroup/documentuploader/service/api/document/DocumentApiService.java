package com.fdmgroup.documentuploader.service.api.document;

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

import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.api.AbstractApiService;

import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link AbstractDocumentApiService} which contains
 * concrete implementations of all methods defined in both the before-mentioned
 * and {@link AbstractApiService} interfaces.
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class DocumentApiService implements AbstractDocumentApiService {

	private static final String PATH_PARAM_ID = "/{id}";
	private static final String ID = "id";

	/**
	 * Used to perform HTTP Requests and retrieve data from the associated HTTP
	 * Responses.
	 */
	private final WebClient webClient;

	public DocumentApiService(@Value("${data.service.url}") String baseUrl) {
		this.webClient = WebClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	@Override
	public Document save(Document document) {
		return webClient
				.post()
				.uri(builder -> builder
						.path(ApiUri.DOCUMENTS.getUri())
						.build())
				.bodyValue(document)
				.retrieve()
				.onStatus(HttpStatus.BAD_REQUEST::equals,
						ClientResponse::createException)
				.bodyToMono(Document.class)
				.toFuture()
				.join();
	}

	@Override
	public Optional<Document> findById(Long id) {
		return webClient
				.get()
				.uri(builder -> builder 
						.path(ApiUri.DOCUMENTS.getUri())
						.queryParam(ID, id)
						.build())
				.retrieve()
				.bodyToMono(Document.class)
				.blockOptional();
	}

	@Override
	public Mono<Document> update(Document document) {
		return webClient
				.put()
				.uri(builder -> builder
						.path(ApiUri.DOCUMENTS.getUri())
						.build())
				.bodyValue(document)
				.retrieve()
				.onStatus(HttpStatus.NOT_FOUND::equals,
						ClientResponse::createException)
				.bodyToMono(Document.class);
	}

	@Override
	public Mono<Document> deleteById(Long id) {
		return webClient
				.delete()
				.uri(builder -> builder
						.path(ApiUri.DOCUMENTS.getUri())
						.path(PATH_PARAM_ID)
						.build(id))
				.retrieve()
				.onStatus(HttpStatus.NOT_FOUND::equals,
						ClientResponse::createException)
				.bodyToMono(Document.class);
	}

	@Override
	public Optional<Document> findByName(String name) {
		return webClient
				.get()
				.uri(builder -> builder
						.path(ApiUri.DOCUMENTS.getUri())
						.queryParam("documentName", name)
						.build())
				.retrieve()
				.bodyToMono(Document.class)
				.blockOptional();
	}

}
