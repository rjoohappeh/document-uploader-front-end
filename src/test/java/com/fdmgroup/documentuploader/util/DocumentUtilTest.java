package com.fdmgroup.documentuploader.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.fdmgroup.documentuploader.model.document.Document;

class DocumentUtilTest {
	
	private static final String TEST_FILE_PATH = "text.docx";
	private static final String TEST = "test";
	private static final String DOCX = "docx";
	private static final String TEXT = "text";
	private static final String FILE = "file";

	private MockMultipartFile mockMultipartFile;
	
	@Test
	void testCreateDocument_throwsException_whenFileIsNull() throws IOException {
		assertThrows(NullPointerException.class, 
				() -> DocumentUtil.createDocument(null));
	}
	
	@Test
	void testCreateDocument_returnsDocument_withSameNameExtensionAndContentAsFileGiven_whenFileNotNull() throws IOException {
		mockMultipartFile = new MockMultipartFile(FILE, TEST_FILE_PATH, TEST, TEST.getBytes());
		Document expected = new Document(TEXT, DOCX, TEST.getBytes());
		Document actual = DocumentUtil.createDocument(mockMultipartFile);
		assertEquals(expected, actual);
	}
}
