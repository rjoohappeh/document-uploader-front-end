package com.fdmgroup.documentuploader.dto.validation.annotation.validator;

import com.fdmgroup.documentuploader.dto.validation.annotation.UniqueEmail;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Performs validation for the {@link UniqueEmail} annotation.
 * 
 * @author Noah.Anderson
 */
public class EmailValidator implements ConstraintValidator<UniqueEmail, String>{

	private final AbstractUserService userService;
	
	@Autowired
	public EmailValidator(AbstractUserService userService) {
		this.userService = userService;
	}
	
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !userService.findByEmail(value).isPresent();
	}
	

}
