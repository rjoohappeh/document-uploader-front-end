package com.fdmgroup.documentuploader.dto.register;

import com.fdmgroup.documentuploader.dto.validation.annotation.UniqueEmail;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.user.User;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Data Transfer Object which encapsulates the information entered by the user 
 * on the registration form. 
 * 
 * @author Noah Anderson
 *
 */
@Component
@Scope("prototype")
public class RegisterDto {

	@Pattern(message = "{first-name.matches-pattern}", regexp = "[A-Za-z]+")
	@NotBlank(message = "{first-name.not-empty}")
	private String firstName;

	@Pattern(message = "{last-name.matches-pattern}", regexp = "[A-Za-z]+")
	@NotBlank(message = "{last-name.not-empty}")
	private String lastName;

	@UniqueEmail
	@Email(message = "{email.matches-pattern}")
	private String email;

	@Pattern(message = "{password.matches-pattern}", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,}$")
	@Length(message = "{password.valid-length}", min = 8, max = 30)
	@NotBlank(message = "{password.not-empty}")
	private String password;

	@Pattern(message = "{password.matches-pattern}", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,}$")
	@Length(message = "{password.valid-length}", min = 8, max = 30)
	@NotBlank(message = "{password.not-empty}")
	private String passwordConfirmation;

//	@UniqueAccountName
	@Length(message = "{account-name.valid-length}")
	@NotBlank(message = "{account-name.not-empty}")
	private String accountName;

	@NotBlank(message = "{service-level.not-empty}")
	private String serviceLevel;

	/**
	 * Creates and returns a {@link Account} object with an <strong>account
	 * name</strong> of of {@link RegisterDto#accountName accountName} and a
	 * {@link ServiceLevel} with a <strong>name</strong> matching that of
	 * {@link RegisterDto#serviceLevel serviceLevel}.
	 *
	 * @return A {@code Account} instance.
	 */
	public Account getAccount() {
		return new Account.AccountBuilder()
					.setName(this.accountName)
					.setServiceLevel(ServiceLevel.valueOf(this.serviceLevel.toUpperCase()))
					.build();
	}

	/**
	 * Creates and returns a {@link User} object with properties that have values
	 * equal to those of {@link RegisterDto#email email},
	 * {@link RegisterDto#password password}, {@link RegisterDto#firstName
	 * firstName}, and {@link RegisterDto#lastName lastName}.
	 *
	 * @return A {@code User} instance.
	 */
	public User getUser() {
		return new User.UserBuilder()
				.setEmail(this.email)
				.setPassword(this.password)
				.setFirstName(this.firstName)
				.setLastName(this.lastName)
				.build();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	@Override
	public String toString() {
		return "RegisterDto [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", password="
				+ password + ", accountName=" + accountName + ", serviceLevel=" + serviceLevel + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((serviceLevel == null) ? 0 : serviceLevel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegisterDto other = (RegisterDto) obj;
		if (accountName == null) {
			if (other.accountName != null)
				return false;
		} else if (!accountName.equals(other.accountName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (serviceLevel != other.serviceLevel)
			return false;
		return true;
	}

}
