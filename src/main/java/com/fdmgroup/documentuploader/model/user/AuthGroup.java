package com.fdmgroup.documentuploader.model.user;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Encapsulates the data related to authentication based access to the system.
 * 
 * @author Noah Anderson
 * @see User
 * @see Role
 */
@Component
@Scope("prototype")
public class AuthGroup {

	private long id;
	private String username;
	private Role role;

	public AuthGroup() {
		super();
	}

	public AuthGroup(String username, Role role) {
		super();
		this.username = username;
		this.role = role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return username;
	}

	public void setEmail(String email) {
		this.username = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
