package com.fdmgroup.documentuploader.dto.login.mapper;

import org.springframework.stereotype.Component;

import com.fdmgroup.documentuploader.dto.login.LoginDto;
import com.fdmgroup.documentuploader.model.user.User;

/**
 * Mapper class which is responsible for converting {@link User} objects
 * into DTOs.
 * 
 * @author Noah Anderson
 *
 */
@Component
public class UserMapper {

	private UserMapper() { }
	/**
	 * Converts a {@link User} object into a {@link LoginDto} object.
	 * 
	 * @param user object to convert into a {@code LoginDto} object
	 * @return created {@code LoginDto} object
	 */
	public static LoginDto toLoginDto(User user) {
		return new LoginDto(user.getEmail(), user.getPassword());
	}
}
