package com.fdmgroup.documentuploader.dto.resetPassword;

import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * Data Transfer Object which encapsulates information required to process a
 * request to reset the password of a
 * {@link com.fdmgroup.documentuploader.model.user.User User}.
 *
 * @author Noah Anderson
 */
@Component
@Scope("prototype")
public class ResetPasswordDto {

	@Email(message = "{email.matches-pattern}")
	@NotBlank(message = "{email.not-empty}")
	private String email;

	@Pattern(message = "{password.matches-pattern}", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,}$")
	@NotBlank(message = "{password.not-empty}")
	@Length(message = "{password.valid-length", min = 8, max = 30)
	private String newPassword;

	@Pattern(message = "{password.matches-pattern}", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,}$")
	@Length(message = "{password.valid-length}", min = 8, max = 30)
	@NotBlank(message = "{password.not-empty}")
	private String newPasswordConfirmation;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

	@Override
	public String toString() {
		return "ResetPasswordDto{" + "email='" + email + '\'' + ", newPassword='" + newPassword + '\''
				+ ", newPasswordConfirmation='" + newPasswordConfirmation + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ResetPasswordDto that = (ResetPasswordDto) o;
		return Objects.equals(email, that.email) && Objects.equals(newPassword, that.newPassword)
				&& Objects.equals(newPasswordConfirmation, that.newPasswordConfirmation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, newPassword, newPasswordConfirmation);
	}
}
