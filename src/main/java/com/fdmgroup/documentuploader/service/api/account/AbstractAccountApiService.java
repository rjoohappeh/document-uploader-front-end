package com.fdmgroup.documentuploader.service.api.account;

import java.util.List;
import java.util.Optional;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.api.AbstractApiService;

/**
 * <p>
 * Interface that defines basic CRUD operations for {@link Account} objects
 * which, when implemented, should send requests to an external API to retrieve
 * information from an external data source.
 * </p>
 * 
 * @author Noah Anderson
 */
public interface AbstractAccountApiService extends AbstractApiService<Account, Long> {

	/**
	 * Attempts to retrieve an {@link Account} instance which is owned by a
	 * {@link User} with an {@code id} equaling the value of {@code ownerId}.
	 * 
	 * @param ownerId the {@code User} {@code id} to search for an {@code Account}
	 *                object with
	 * @return {@code empty} {@link Optional} if no account is found which is owned
	 *         by a {@code User} with an id of {@code ownerId}. Otherwise, an
	 *         {@code Optional} encapsulating the found {@code Account} object is
	 *         returned
	 */
	Optional<Account> findByOwnerId(long ownerId);

	/**
	 * Attempts to retrieve an {@link Account} instance with a name equaling the
	 * value of the given {@code name}.
	 * 
	 * @param name the {@code Account} name to search for
	 * @return {@code empty} {@link Optional} if no account is found with a name
	 *         equaling the given {@code name}. Otherwise, an {@code Optional}
	 *         encapsulating the found {@code Account} object is returned
	 */
	Optional<Account> findByName(String name);

	/**
	 * Retrieves all {@link Account} objects which a {@link User} with an {@code id}
	 * equaling the given {@code userId} can access.
	 * 
	 * @param userId the {@code id} of a {@code User}
	 * @return {@link List} containing all {@code Account} objects which are
	 *         accessible to a {@code User} with an {@code id} equaling the given
	 *         {@code userId}
	 */
	List<Account> findAccountsByUserId(long userId);

	/**
	 * Adds the given {@link Document} to an {@link Account} with an {@code id}
	 * equaling the value of {@code accountId} and returns the updated
	 * {@code Account} instance.
	 * 
	 * @param document  the {@code Document} to be added to an {@code Account}
	 * @param accountId the {@code id} of the {@code Account} to add
	 * @return the updated {@code Account} instance.
	 */
	Account addDocumentToAccountByAccountId(Document document, long accountId);

	/**
	 * Removes a {@link Document} with a {@code name} equal to {@code documentName}
	 * from an {@link Account} with an {@code id} equal to {@code accountId}.
	 *
	 * @param documentName the {@code name} of a {@code Document}
	 * @param accountId    the {@code id} of an {@code Account}
	 * @return the updated {@code Account}
	 */
	Account removeDocumentFromAccountByAccountId(String documentName, long accountId);
}
