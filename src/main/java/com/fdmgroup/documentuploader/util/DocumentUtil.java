package com.fdmgroup.documentuploader.util;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.fdmgroup.documentuploader.model.document.Document;

/**
 * Utility class which has static methods for manipulating and creating
 * {@link Document} objects.
 * 
 * @author Noah Anderson
 *
 */
public class DocumentUtil {

	private static final String DOT_SEPARATOR_REGEX = "\\.";

	private DocumentUtil() { }

	/**
	 * Given a {@link MultipartFile}, creates a {@link Document} based on the
	 * properties of the {@code MultipartFile}.
	 * 
	 * @param file the {@code MultipartFile} used to create a {@code Document}
	 * @return created {@code Document} object
	 * @throws IOException if temporary storage of the {@code file} given fails
	 */
	public static Document createDocument(@NotNull MultipartFile file) throws IOException {
		Document document = new Document();
		String originalFileName = file.getOriginalFilename();
		if (originalFileName != null) {
			String[] documentInfo = originalFileName.split(DOT_SEPARATOR_REGEX);
			document.setName(documentInfo[0]);
			document.setExtension(documentInfo[1]);
			document.setContent(file.getBytes());
		}
		return document;
	}

}
