package com.fdmgroup.documentuploader.service.api.document;

import java.util.Optional;

import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.api.AbstractApiService;

/**
 * <p>
 * Interface that defines basic CRUD operations for {@link Document} objects
 * which, when implemented, should send requests to an external API to retrieve
 * information from an external data source.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractDocumentApiService extends AbstractApiService<Document, Long> {

	/**
	 * Attempts to retrieve an {@link Document} instance with a name equaling the
	 * value of the given {@code name}.
	 * 
	 * @param name the {@code Document} name to search for
	 * @return {@code empty} {@link Optional} if no account is found with a name
	 *         equaling the given {@code name}. Otherwise, an {@code Optional}
	 *         encapsulating the found {@code Document} object is returned
	 */
	Optional<Document> findByName(String name);

}
