package com.fdmgroup.documentuploader.service.account;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fdmgroup.documentuploader.exception.CannotAddUserToAccountException;
import com.fdmgroup.documentuploader.exception.CannotRemoveUserFromAccountException;
import com.fdmgroup.documentuploader.exception.FileException;
import com.fdmgroup.documentuploader.exception.InvalidServiceLevelException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.account.AbstractAccountApiService;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import com.fdmgroup.documentuploader.util.DocumentUtil;

/**
 * <p>
 * Implementing class of {@link AbstractAccountService} which performs
 * operations related to {@link Account} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
@Primary
@Service
public class AccountService implements AbstractAccountService {

	/**
	 * Used to perform operations on and retrieve {@link User} objects.
	 */
	private final AbstractUserService userService;

	/**
	 * Used to perform API calls to save, update, read, or remove {@link Account}
	 * objects.
	 */
	private final AbstractAccountApiService accountApiService;

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;

	@Autowired
	public AccountService(AbstractUserService userService, AbstractAccountApiService accountApiService,
			MessageSource messageSource) {
		this.userService = userService;
		this.accountApiService = accountApiService;
		this.messageSource = messageSource;
	}

	@Override
	public Account addUserToAccountByEmail(String email, Account account) {
		if (isEmailOnAccount(email, account)) {
			throw new CannotAddUserToAccountException(
					messageSource.getMessage("account.user-on-account", null, Locale.getDefault()));
		}
		if (account.hasMaxUsers()) {
			throw new CannotAddUserToAccountException(
					messageSource.getMessage("account.max-users", null, Locale.getDefault()));
		}
		Optional<User> optionalUser = userService.findByEmail(email);
		if (!optionalUser.isPresent()) {
			throw new CannotAddUserToAccountException(
					messageSource.getMessage("account.email-not-found", null, Locale.getDefault()));
		}

		User user = optionalUser.get();
		account = addUserToAccount(user, account);
		accountApiService.update(account).subscribe();

		return account;
	}

	/**
	 * Checks if the {@code email} given belongs to a {@link User} on the
	 * {@link Account}.
	 *
	 * @param email   the email which may or may not belong to a {@code User} on the
	 *                account
	 * @param account the {@code Account} object which may or may not have a
	 *                {@code User} with an email equal to {@code email}
	 * @return {@code true} if the {@code email} is on the {@code account}, false
	 *         otherwise
	 */
	private boolean isEmailOnAccount(String email, Account account) {
		return account.getUsers().stream().anyMatch(e -> e.getEmail().equals(email));
	}

	/**
	 * Adds the given {@link User} to the given {@link Account}.
	 *
	 * @param user    the {@code User} to add to {@code account}
	 * @param account the {@code Account} to add the {@code user} to
	 * @return an {@code Account} instance which has the given {@code User user} in
	 *         its Set of users
	 */
	private Account addUserToAccount(User user, Account account) {
		Set<User> accountUsers = account.getUsers();
		accountUsers.add(user);
		account.setUsers(accountUsers);

		return account;
	}

	@Override
	public Account removeUserFromAccountById(long id, User user, Account account) {
		long userId = user.getId();
		if (id == userId) {
			throw new CannotRemoveUserFromAccountException(
					messageSource.getMessage("account.can-not-remove-self", null, Locale.getDefault()));
		}
		Optional<User> optionalUser = userService.findById(id);
		if (!optionalUser.isPresent()) {
			throw new CannotRemoveUserFromAccountException(
					messageSource.getMessage("account.user-not-found", null, Locale.getDefault()));
		}

		Set<User> users = account.getUsers();
		User guest = optionalUser.get();
		users.remove(guest);
		account.setUsers(users);
		accountApiService.update(account).block();

		return account;
	}

	@Override
	public Account updateAccountServiceLevel(Account account, String serviceLevelName) {
		Optional<ServiceLevel> optionalServiceLevel = getServiceLevelByName(serviceLevelName);
		if (!optionalServiceLevel.isPresent()) {
			throw new InvalidServiceLevelException(
					messageSource.getMessage("account.invalid-service-level", null, Locale.getDefault()));
		}

		ServiceLevel selectedServiceLevel = optionalServiceLevel.get();
		account.setServiceLevel(selectedServiceLevel);
		accountApiService.update(account).block();

		return account;
	}

	/**
	 * Gets a {@link ServiceLevel} with a name equal to {@code serviceLevelName}.
	 *
	 * @param serviceLevelName the name of a {@code ServiceLevel}
	 * @return an {@code Optional} wrapping the {@code ServiceLevel} if the value of
	 *         {@code serviceLevelName} equals the {@code name} of any
	 *         {@code ServiceLevel}, if not, then an empty {@code Optional} is
	 *         returned
	 */
	private Optional<ServiceLevel> getServiceLevelByName(final String serviceLevelName) {
		EnumSet<ServiceLevel> serviceLevels = EnumSet.allOf(ServiceLevel.class);

		return serviceLevels.stream().filter(e -> e.getName().equalsIgnoreCase(serviceLevelName)).findFirst();
	}

	@Override
	@Transactional
	public Account addFileToAccount(MultipartFile file, Account account) {
		if (isFileOnAccount(account, file)) {
			throw new FileException(
					messageSource.getMessage("account.file-on-account", null, Locale.getDefault()));
		}
		if (file.isEmpty()) {
			throw new FileException(
					messageSource.getMessage("account.choose-file", null, Locale.getDefault()));
		}

		try {
			return uploadFile(account, file);
		} catch (IOException e) {
			throw new FileException(
					messageSource.getMessage("account.could-not-upload", null, Locale.getDefault()));
		}
	}

	/**
	 * Checks if the file name of {@link MultipartFile file} matches the name of any
	 * {@link Document} within the {@code Set} of {@code Document} objects belonging
	 * to the given {@link Account}
	 *
	 * @param account the {@code Account} which may have a {@code Document} with a
	 *                name matching the name of the given {@code file}
	 * @param file    the {@code file} which may or may not have a name belonging to
	 *                a {@code Document} on the {@code Account} given
	 * @return {@code true} if file name of {@link MultipartFile file} matches the
	 *         name of any {@link Document} within the {@code Set} of
	 *         {@code Document} objects belonging to the given {@link Account},
	 *         {@code false} otherwise
	 */
	private boolean isFileOnAccount(Account account, MultipartFile file) {
		String fileName = file.getName();
		return account.getDocuments().stream().anyMatch(document -> fileName.equals(document.getName()));
	}

	/**
	 * Adds the given {@link MultipartFile} to the given {@link Account}.
	 *
	 * @param account the {@code Account} to add the {@code file} to
	 * @param file    the {@code MultipartFile} to add to the given {@code account}
	 * @return the updated {@code Account} instance
	 * @throws IOException if an error occurs while trying to upload the file to the
	 *                     data source
	 */
	private Account uploadFile(Account account, MultipartFile file) throws IOException {
		Document document = DocumentUtil.createDocument(file);
		Account updatedAccount = accountApiService.addDocumentToAccountByAccountId(document, account.getId());
		account.setDocuments(updatedAccount.getDocuments());
		return account;
	}

	@Override
	@Transactional
	public Account removeFileFromAccountByFileName(String fileName, Account account) {
		Set<Document> accountDocuments = account.getDocuments();
		List<Document> documentsWithFileName = accountDocuments.stream()
				.filter(document -> document.getName().equals(fileName)).collect(Collectors.toList());
		if (documentsWithFileName.isEmpty()) {
			throw new FileException(fileName +
					messageSource.getMessage("account.file-does-not-exist", null, Locale.getDefault()));
		}

		Account updatedAccount = accountApiService.removeDocumentFromAccountByAccountId(fileName, account.getId());
		account.setDocuments(updatedAccount.getDocuments());
		return account;
	}

	@Override
	public List<Account> getAllAccountsByUserId(long userId) {
		return accountApiService.findAccountsByUserId(userId);
	}

	@Override
	public Optional<Account> findByOwnerId(long id) {
		return accountApiService.findByOwnerId(id);
	}

	/**
	 * Checks if an {@link Account} with a name equaling the given {@code name}
	 * exists.
	 *
	 * @param name the possible {@code account} name
	 * @return {@code false} if an {@code account} with the given {@code name} does
	 *         exist. Otherwise, returns {@code false}
	 */
	public boolean accountNameExists(String name) {
		return accountApiService.findByName(name).isPresent();
	}

}
