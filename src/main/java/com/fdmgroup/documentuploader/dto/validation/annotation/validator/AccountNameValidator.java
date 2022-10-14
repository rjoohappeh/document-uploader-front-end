package com.fdmgroup.documentuploader.dto.validation.annotation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.fdmgroup.documentuploader.dto.validation.annotation.UniqueAccountName;
import com.fdmgroup.documentuploader.service.account.AccountService;

/**
 * Performs validation for the {@link UniqueAccountName} annotation.
 * 
 * @author Noah.Anderson
 */
public class AccountNameValidator implements ConstraintValidator<UniqueAccountName, String> {

	private final AccountService accountService;

	@Autowired
	public AccountNameValidator(AccountService accountService) {
		this.accountService = accountService;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !accountService.accountNameExists(value);
	}

}
