package com.fdmgroup.documentuploader.dto.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.fdmgroup.documentuploader.dto.validation.annotation.validator.AccountNameValidator;

/**
 * <p>Indicates that the value of the annotated account name field must be unique
 * in the database.</p>
 * <p>This constraint is validated by the {@link AccountNameValidator} class may only be 
 * applied to a {@link ElementType#FIELD field} of a class.</p>
 * 
 * @author Noah.Anderson
 */
@Documented
@Constraint(validatedBy = AccountNameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueAccountName {

	/**
	 * Sets the message to be used when the value of the annotated field does not satisfy the requirements
	 * determined by {@link AccountNameValidator#isValid(String, javax.validation.ConstraintValidatorContext) AccountNameValidator.isValid()}
	 *
	 * @return the message to be used upon validation failure
	 */
	String message() default "{account-name.is-used}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
