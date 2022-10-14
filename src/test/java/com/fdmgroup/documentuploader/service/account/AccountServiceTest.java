package com.fdmgroup.documentuploader.service.account;

import com.fdmgroup.documentuploader.exception.CannotAddUserToAccountException;
import com.fdmgroup.documentuploader.exception.CannotRemoveUserFromAccountException;
import com.fdmgroup.documentuploader.exception.FileException;
import com.fdmgroup.documentuploader.exception.InvalidServiceLevelException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.account.AccountApiService;
import com.fdmgroup.documentuploader.service.user.UserService;
import com.fdmgroup.documentuploader.util.DocumentUtil;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

	private static final String EMAIL = "testEmail@email.com";
	private static final String INVALID_SERVICE_LEVEL = "levelThatDoesNotExist";
	private static final String BRONZE_SERVICE_LEVEL_NAME = "bronze";
	private static final String TEST = "test";
	private static final String FILE = "file";
	private static final String TEST_FILE_PATH = "text.docx";

	private AccountService accountService;

	@Mock
	private User mockUser;

	@Mock
	private Account mockAccount;

	@Mock
	private Document mockDocument;

	@Mock
	private UserService mockUserService;

	@Mock
	private AccountApiService mockAccountApiService;

	@Mock
	private MessageSource mockMessageSource;

	private MockMultipartFile mockMultipartFile;

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.accountService = new AccountService(mockUserService, mockAccountApiService, mockMessageSource);
	}

	@Test
	void testRemoveUserWithIdFromAccount_throwsCannotRemoveUserFromAccountException_ifIdEqualsUserId() {
		assertThrows(CannotRemoveUserFromAccountException.class, () -> accountService.removeUserFromAccountById(0, mockUser, mockAccount));
	}

	@Test
	void testRemoveUserWithIdFromAccount_throwsCannotRemoveUserFromAccountException_ifIdDoesNotExist() {
		when(mockUserService.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(CannotRemoveUserFromAccountException.class, () -> accountService.removeUserFromAccountById(1, mockUser, mockAccount));
	}

	@Test
	void testRemoveUserWithIdFromAccount_removesUserFromAccount_andReturnsUpdatedAccount_ifNoExceptionIsThrown()
			throws CannotRemoveUserFromAccountException {
		Set<User> accountUsers = new HashSet<>();
		accountUsers.add(mockUser);
		when(mockAccount.getUsers()).thenReturn(accountUsers);
		when(mockUserService.findById(anyLong())).thenReturn(Optional.of(mockUser));
		when(mockAccountApiService.update(mockAccount)).thenReturn(Mono.just(mockAccount));

		Account result = accountService.removeUserFromAccountById(1, mockUser, mockAccount);
		Set<User> resultUsers = result.getUsers();

		assertFalse(resultUsers.contains(mockUser));
	}

	@Test
	void testAddUserWithEmailToAccount_throwsCannotAddUserToAccountException_ifEmailBelongsToUserOnAccountAlready() {
		Set<User> accountUsers = new HashSet<>();
		accountUsers.add(mockUser);
		when(mockUser.getEmail()).thenReturn(EMAIL);
		when(mockAccount.getUsers()).thenReturn(accountUsers);

		assertThrows(CannotAddUserToAccountException.class, () -> accountService.addUserToAccountByEmail(EMAIL, mockAccount));
	}

	@Test
	void testAddUserWithEmailToAccount_throwsCannotAddUserToAccountException_ifAccountHasMaxUsers() {
		when(mockAccount.hasMaxUsers()).thenReturn(true);
		assertThrows(CannotAddUserToAccountException.class, () -> accountService.addUserToAccountByEmail(EMAIL, mockAccount));
	}

	@Test
	void testAddUserWithEmailToAccount_throwsCannotAddUserToAccountException_ifEmailDoesNotExist() {
		when(mockAccount.hasMaxUsers()).thenReturn(false);
		when(mockAccount.getUsers()).thenReturn(Collections.emptySet());
		when(mockUserService.findByEmail(EMAIL)).thenReturn(Optional.empty());
		assertThrows(CannotAddUserToAccountException.class, () -> accountService.addUserToAccountByEmail(EMAIL, mockAccount));
	}

	@Test
	void testAddUserWithEmailToAccount_returnsAccountWithUserInSetOfUsers_ifNoExceptionsAreThrown()
			throws CannotAddUserToAccountException {
		when(mockAccount.hasMaxUsers()).thenReturn(false);
		when(mockAccount.getUsers()).thenReturn(new HashSet<>());
		when(mockAccountApiService.update(mockAccount)).thenReturn(Mono.just(mockAccount));
		when(mockUserService.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser));

		Set<User> expectedAccountUsers = new HashSet<>();
		expectedAccountUsers.add(mockUser);

		Account actualAccount = accountService.addUserToAccountByEmail(EMAIL, mockAccount);
		Set<User> actualAccountUsers = actualAccount.getUsers();

		assertEquals(expectedAccountUsers, actualAccountUsers);
	}

	@Test
	void testEditServiceLevel_throwsInvalidServiceLevelException_whenNonExistentServiceLevelIsGiven() {
		assertThrows(InvalidServiceLevelException.class, () -> accountService.updateAccountServiceLevel(mockAccount, INVALID_SERVICE_LEVEL));
	}

	@Test
	void testEditServiceLevel_returnsAccountWithSelectedServiceLevel_whenExistingServiceLevelIsGiven()
			throws InvalidServiceLevelException {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.BRONZE);
		when(mockAccountApiService.update(mockAccount)).thenReturn(Mono.just(mockAccount));

		ServiceLevel expectedAccountServiceLevel = ServiceLevel.BRONZE;

		Account actualAccount = accountService.updateAccountServiceLevel(mockAccount, BRONZE_SERVICE_LEVEL_NAME);
		ServiceLevel actualServiceLevel = actualAccount.getServiceLevel();

		assertEquals(expectedAccountServiceLevel, actualServiceLevel);
	}

	@Test
	void testAddFileToAccount_throwsFileException_whenFileIsAlreadyOnAccount() throws IOException {
		Set<Document> accountDocuments = new HashSet<>();
		mockMultipartFile = new MockMultipartFile("text", TEST_FILE_PATH, TEST, TEST.getBytes());
		accountDocuments.add(DocumentUtil.createDocument(mockMultipartFile));
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		assertThrows(FileException.class, () -> accountService.addFileToAccount(mockMultipartFile, mockAccount));
	}

	@Test
	void testAddFileToAccount_throwsFileException_whenFileIsEmpty() throws IOException {
		Set<Document> accountDocuments = new HashSet<>();
		mockMultipartFile = new MockMultipartFile("text", TEST_FILE_PATH, TEST, Strings.EMPTY.getBytes());
		when(mockAccount.getDocuments()).thenReturn(accountDocuments);
		assertThrows(FileException.class, () -> accountService.addFileToAccount(mockMultipartFile, mockAccount));
	}

	@Test
	void testRemoveFileFromAccount_throwsFileException_whenNoFileWithTheGivenNameExistsOnTheAccount()
			throws IOException {
		when(mockAccount.getDocuments()).thenReturn(new HashSet<>());
		assertThrows(FileException.class, () -> accountService.removeFileFromAccountByFileName(FILE, mockAccount));
	}

	@Test
	void testRemoveFileFromAccount_callsAccountApiServiceRemoveDocumentFromAccountByAccountId() throws FileException {
		Set<Document> documentSet = new HashSet<>();
		documentSet.add(mockDocument);
		when(mockAccount.getDocuments()).thenReturn(documentSet);
		when(mockDocument.getName()).thenReturn(FILE);
		when(mockAccountApiService.removeDocumentFromAccountByAccountId(FILE, 0L)).thenReturn(mockAccount);

		accountService.removeFileFromAccountByFileName(FILE, mockAccount);

		verify(mockAccountApiService, times(1)).removeDocumentFromAccountByAccountId(FILE, 0L);
	}

}
