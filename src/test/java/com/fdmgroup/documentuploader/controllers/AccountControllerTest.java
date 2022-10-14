package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.exception.FileException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.account.AccountService;
import com.fdmgroup.documentuploader.service.document.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@EnableConfigurationProperties(value = ApplicationProperties.class)
@PropertySource(value = { "classpath:/paths.properties" })
class AccountControllerTest {
	
	private static final String SLASH = "/";
	private static final String TEST = "test";
	private static final String FILE = "file";
	private static final String TEST_FILE_PATH = "text.docx";
	private static final String FILE_ALREADY_UPLOADED = "File already uploaded!";

	@Autowired
	private ApplicationProperties applicationProperties;
	private RequestUris requestUris;
	
	@Value("${app.request-uris.account}")
	private String accountUri;
	@Value("${app.request-uris.delete-document}")
	private String deleteDocumentUri;
	@Value("${app.request-uris.download-document}")
	private String downloadDocumentUri;
	
	@Mock
	private User mockUser;
	
	@Mock
	private Account mockAccount;
	
	@MockBean(name="document")
	private Document mockDocument;
	
	@MockBean
	private AccountService mockAccountService;
	
	@MockBean
	private DocumentService mockDocumentService;

	@Autowired
	private MockMvc mockMvc;
	
	private MockMultipartFile file;

	@MockBean
	Authentication mockAuth;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.requestUris = applicationProperties.getRequestUris();
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testUploadFile_throws_fileException_whenAccountService_uploadFile_throwsIt() throws Exception {
		file = new MockMultipartFile(FILE, TEST_FILE_PATH, TEST, TEST.getBytes());
		when(mockAccountService.addFileToAccount(file, mockAccount)).thenThrow(new FileException(FILE_ALREADY_UPLOADED));
		
		MvcResult result = mockMvc.perform(multipart(accountUri)
							.file(file)
							.sessionAttr(AttributeName.USER.getValue(), mockUser)
							.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
							.andExpect(matchAll(
									status().is3xxRedirection(),
									redirectedUrl(requestUris.getLogin())))
							.andReturn();
		
		assertTrue(result.getResolvedException() instanceof FileException);
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testUploadFile_callsSaveOfDocumentService_andUpdateOfAccountService() throws Exception {
		file = new MockMultipartFile(FILE, TEST_FILE_PATH, TEST, TEST.getBytes());
		when(mockAccountService.addFileToAccount(file, mockAccount)).thenReturn(mockAccount);
		when(mockAccount.getName()).thenReturn("accountName");

		MvcResult result = mockMvc.perform(multipart(accountUri)
							.file(file)
							.sessionAttr(AttributeName.USER.getValue(), mockUser)
							.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
							.andExpect(matchAll(
									status().is3xxRedirection(),
									redirectedUrl(requestUris.getAccount() + "/accountName")))
							.andReturn();
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testDeleteDocument_addsMessage_nameWasNotDeletedFromTheAccount_whenDocumentDoesntExist() throws Exception {
		when(mockAccountService.removeFileFromAccountByFileName(FILE, mockAccount)).thenReturn(mockAccount);
		when(mockAccount.getName()).thenReturn("accountName");

		file = new MockMultipartFile(FILE, TEST_FILE_PATH, TEST, TEST.getBytes());
		
		mockMvc.perform(get(accountUri + SLASH + deleteDocumentUri + SLASH + file.getName())
							.sessionAttr(AttributeName.USER.getValue(), mockUser)
							.sessionAttr(AttributeName.ACCOUNT.getValue(), mockAccount))
							.andExpect(matchAll(
									status().is3xxRedirection(),
									redirectedUrl(requestUris.getAccount() + "/accountName")));
	}
	
	@Test
	@WithMockUser(roles = "USER")
	void testDownloadDocument_returnsResponseEntity_withOkStatusAndFileContents() throws Exception {
		file = new MockMultipartFile("file", TEST_FILE_PATH, TEST, TEST.getBytes());
		
		when(mockDocument.getContent()).thenReturn(file.getBytes());
		when(mockDocumentService.findByName(anyString())).thenReturn(Optional.of(mockDocument));
		
		MvcResult result = mockMvc.perform(get(accountUri + SLASH + downloadDocumentUri + SLASH + TEST)
							.sessionAttr(AttributeName.USER.getValue(), mockUser))
							.andExpect(status().isOk())
							.andReturn();
		
		String content = result.getResponse().getContentAsString();
		String expected = new String(mockDocument.getContent(), StandardCharsets.UTF_8);
		
		assertEquals(expected, content);
	}
}
