package com.fdmgroup.documentuploader.exception;

import org.springframework.web.multipart.MultipartFile;

/**
 * Exception thrown when an error occurs while performing operations involving 
 * {@link MultipartFile} objects.
 * 
 * @author Noah Anderson
 */
public class FileException extends RuntimeException {

	private static final long serialVersionUID = 4265550803542695717L;

	public FileException() {
		super();
	}

	public FileException(String message) {
		super(message);
	}
	
	
}
