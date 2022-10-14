package com.fdmgroup.documentuploader.security;

import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.user.AbstractUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementing class of {@link UserDetails} and is used as a means
 * of storing {@link User} information related to security such as their
 * {@link AuthGroup}s.
 * 
 * @author Noah Anderson
 */
public class AppUserPrincipal implements UserDetails {

	private static final long serialVersionUID = 4495843355464014174L;
	private final AbstractUserService userService;
	private final User user;
	private final List<AuthGroup> authGroups;

	public AppUserPrincipal(AbstractUserService userService, User user, List<AuthGroup> authGroups) {
		super();
		this.userService = userService;
		this.user = user;
		this.authGroups = authGroups;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (null == authGroups) {
			return Collections.emptySet();
		}
		Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
		authGroups.forEach(group -> grantedAuthorities.add(new SimpleGrantedAuthority(group.getRole().name())));
		return grantedAuthorities;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return userService.hasActivatedAccount(this.user.getEmail());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AppUserPrincipal that = (AppUserPrincipal) o;
		return userService.equals(that.userService) &&
				user.equals(that.user) &&
				authGroups.equals(that.authGroups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userService, user, authGroups);
	}
}
