package com.fdmgroup.documentuploader.service.document;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.service.api.document.AbstractDocumentApiService;
import com.fdmgroup.documentuploader.util.DocumentUtil;


/**
 * <p>
 * Implementing class of {@link AbstractDocumentService} which performs
 * operations related to {@link Document} objects.
 * </p>
 * 
 * @author Noah Anderson
 * @see AbstractDocumentService
 * @see Document
 */
@Primary
@Service
public class DocumentService implements AbstractDocumentService {

	/**
	 * Used to perform API calls to save, update, read, or remove {@link Document} objects.
	 */
	private final AbstractDocumentApiService documentApiService;

	@Autowired
	public DocumentService(AbstractDocumentApiService documentApiService) {
		super();
		this.documentApiService = documentApiService;
	}

	@Override
	public Document uploadFile(MultipartFile file) throws IOException {
		Document document = DocumentUtil.createDocument(file);
		return documentApiService.save(document);
	}

	/**
	 * Sends an HTTP Request to a REST API endpoint to find an {@link Document} object with an id
	 * equal to {@code id}.
	 * 
	 * @param id the {@code id} to search for a {@link Document} object with
	 * @return empty {@link Optional} if no document is found with the given {@code id}, <br/>
	 * 		   {@link Optional} encapsulating the found {@link Document} object otherwise
	 * @see Document
	 * @see Optional
	 */
	public Optional<Document> findById(long id) {
		return documentApiService.findById(id);
	}

	/**
	 * Sends an HTTP Request to a REST API endpoint to find an {@link Document} object with a name
	 * equal to {@code name}
	 * 
	 * @param fileName the document name to search for a {@link Document} object with
	 * @return empty {@link Optional} if no document is found with the given {@code name}, <br/>
	 * 		   {@link Optional} encapsulating the found {@link Document} object otherwise
	 * @see Document
	 * @see Optional
	 */
	public Optional<Document> findByName(String fileName) {
		return documentApiService.findByName(fileName);
	}
	
	/**
	 * Sends an HTTP Request to a REST API endpoint to delete the given {@linkplain Document} object.
	 * 
	 * @param document the document to delete
	 */
	public void delete(Document document) {
		documentApiService.deleteById(document.getId()).subscribe();
	}

}
