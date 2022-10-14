package com.fdmgroup.documentuploader.service.document;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.fdmgroup.documentuploader.model.document.Document;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to the
 * modification, retrieval, addition, or removal of {@link Document} instances
 * in the system.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractDocumentService {

	/**
	 * Saves and returns a {@link Document} object created from the given
	 * {@link MultipartFile file}.
	 * 
	 * @param file file the {@code MultipartFile} used to create a {@code Document}
	 * @return saved {@link Document} object
	 * @throws IOException if temporary storage of the {@code file} given fails
	 */
	Document uploadFile(MultipartFile file) throws IOException;

	/**
	 * Retrieves a {@link Document} with a name matching the value of
	 * {@code fileName}.
	 * 
	 * @param fileName the document name to search for a {@link Document} object with
	 * @return {@code empty} {@link Optional} if no document is found with the given
	 *         {@code name}, <br/>
	 *         {@link Optional} encapsulating the found {@link Document} object
	 *         otherwise
	 */
	Optional<Document> findByName(String fileName);

	/**
	 * Removes a {@link Document} from the data source.
	 * 
	 * @param document the document to delete
	 */
	void delete(Document document);

}
