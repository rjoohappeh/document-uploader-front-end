package com.fdmgroup.documentuploader.service.account;

import com.fdmgroup.documentuploader.exception.CannotAddUserToAccountException;
import com.fdmgroup.documentuploader.exception.FileException;
import com.fdmgroup.documentuploader.exception.InvalidServiceLevelException;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Interface that defines behaviors to be implemented that pertain to operations
 * dealing with {@link Account} objects.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractAccountService {

	/**
	 * Adds a {@link User} with an email equal to {@code email} to the given
	 * {@link Account}.
	 * 
	 * @param email   the email of the {@code User} to add to the {@code account}
	 * @param account the {@code Account} to add a {@code user} to
	 * @return the updated {@code Account} object
	 * @throws CannotAddUserToAccountException when a {@code User} with an email
	 *                                         equal to {@code email} cannot be
	 *                                         added to the {@code account} for any
	 *                                         reason.
	 */
	Account addUserToAccountByEmail(String email, Account account);

	/**
	 * Removes a {@link User} with the given {@code id} from the given
	 * {@link Account}.
	 * 
	 * @param id      the {@code id} of the {@code User} to remove from
	 *                {@code account}
	 * @param user    the current user of the application
	 * @param account the {@code Account} to remove the {@code user} from
	 * @return the updated {@code Account} instance
	 */
	Account removeUserFromAccountById(long id, User user, Account account);

	/**
	 * Updates the given {@link Account} with a {@link ServiceLevel} with a name
	 * equal to {@code serviceLevelName}
	 *
	 * @param account      	   the {@code Account} to update
	 * @param serviceLevelName the name of a {@code ServiceLevel} to update the
	 *                     {@code account} with
	 * @return the updated {@code Account} instance
	 * @throws InvalidServiceLevelException when the {@code account} cannot be given
	 *                                      a {@code ServiceLevel} with a name equal
	 *                                      to {@code serviceLevel} for any reason.
	 */
	Account updateAccountServiceLevel(Account account, String serviceLevelName);

	/**
	 * Uploads the given {@link MultipartFile} to the data source, and adds it to
	 * the {@code Set} of {@link Document} objects belonging to the {@link Account}
	 *
	 * @param file    the {@code MultipartFile} to upload
	 * @param account the {@code account} to add the {@code Document} created from
	 *                the given {@code file} to
	 * @return the updated {@code Account}
	 * @throws FileException if the given {@code file} is empty, or has already been
	 *                       uploaded to the {@code account}.
	 */
	Account addFileToAccount(MultipartFile file, Account account);

	/**
	 * Removes a file from the given {@link Account} with a name equal to
	 * {@code fileName}.
	 * 
	 * @param fileName the name of the file to remove from the {@code account}
	 * @param account  the {@code Account} to remove a file with a name equal to
	 *                 {@code fileName} from
	 * @return the updated {@code Account}
	 * @throws FileException if a file with the given {@code fileName} does not
	 *                       exist.
	 */
	Account removeFileFromAccountByFileName(String fileName, Account account);

	/**
	 * Finds all the {@link Account} objects which are accessible by {@link User}
	 * which has an {@code id} equaling the given {@code userId}.
	 *
	 * @param userId the possible {@code user} id
	 * @return a {@link List} of {@code Account}
	 */
	List<Account> getAllAccountsByUserId(long userId);

	/**
	 * Finds an {@link Account} object with an owner id equal to {@code id}.
	 *
	 * @param id the owner {@code id} to search for an {@code Account} object with
	 * @return empty {@link Optional} if no account is found with the given owner
	 *         {@code id}, <br/>
	 *         {@code Optional} encapsulating the found {@code Account} object
	 *         otherwise
	 */
	Optional<Account> findByOwnerId(long id);
}
