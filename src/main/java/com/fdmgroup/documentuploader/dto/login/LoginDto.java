package com.fdmgroup.documentuploader.dto.login;

/**
 * Data Transfer Object which encapsulates the information entered by the user
 * on the login form.
 * 
 * @author Noah Anderson
 *
 */
public class LoginDto {

	private String email;
	private String password;
	
	public LoginDto() {
		super();
	}

	public LoginDto(String email, String password) {
		super();
		this.email = email;
		this.password = password;
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

	@Override
	public String toString() {
		return "LoginDto{" +
				"email='" + email + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
