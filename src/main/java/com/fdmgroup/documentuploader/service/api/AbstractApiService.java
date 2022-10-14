package com.fdmgroup.documentuploader.service.api;

import java.util.Optional;

import reactor.core.publisher.Mono;

/**
 * <p>
 * Interface that defines basic CRUD operations which, when implemented, should
 * send requests to an external API to retrieve information from an external
 * data source.
 * </p>
 * 
 * @author Noah Anderson
 *
 * @param <T>  The {@code class} to perform CRUD operations on
 * @param <ID> The {@code type} of the unique identifier of {@code T}
 */
public interface AbstractApiService<T, ID> {

	/**
	 * Saves the given object of type {@code T} and returns it for use with
	 * additional operations.
	 *
	 * @param t the object to save
	 * @return the saved instance to use for further operations
	 */
	T save(T t);

	/**
	 * Attempts to retrieve an instance of type {@code T} by the {@code id} given.
	 * 
	 * @param id the {@code id} of an instance of type {@code T}
	 * @return {@code empty} {@link Optional} if no instance of type {@code T} is
	 *         found with the given {@code id}. Otherwise, an {@code Optional}
	 *         wrapping the found instance of type {@code T} is returned
	 */
	Optional<T> findById(ID id);

	/**
	 * Updates an existing instance of type {@code T} that has an id matching the
	 * value of the given {@code id} with the properties of the given instance of
	 * {@code T}.
	 * 
	 * @param t t the object containing the values to use when updating
	 * @return a {@link Mono} wrapping the updated object
	 */
	Mono<T> update(T t);

	/**
	 * Removes an instance of type {@code T} with an {@code id} matching the value
	 * of the given {@code id}.
	 * 
	 * @param id the {@code id} of an instance of type {@code T}
	 * @return a {@link Mono} wrapping the deleted object
	 */
	Mono<T> deleteById(ID id);
}
