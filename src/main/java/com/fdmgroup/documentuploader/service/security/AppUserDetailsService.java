package com.fdmgroup.documentuploader.service.security;

import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.security.AppUserPrincipal;
import com.fdmgroup.documentuploader.security.ApplicationSecurityConfiguration;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Service class used in the {@link ApplicationSecurityConfiguration} class as a
 * way to create {@link UserDetails} objects based on a {@code username} of a
 * User.
 * </p>
 * <p>
 * The {@link UserDetails} objects are created within and returned from the
 * custom implementation of the {@link #loadUserByUsername(String)} method in
 * this class.
 * </p>
 * 
 * @author Noah Anderson
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

	private final UserService userService;
	private final AuthGroupService authGroupService;

	@Autowired
	public AppUserDetailsService(UserService userService, AuthGroupService authGroupService) {
		super();
		this.userService = userService;
		this.authGroupService = authGroupService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userService.findByEmail(username);
		if (!optionalUser.isPresent()) {
			throw new UsernameNotFoundException("cannot find username: " + username);
		}

		List<AuthGroup> authGroups = authGroupService.findByUsername(username);
		User user = optionalUser.get();

		return new AppUserPrincipal(userService, user, authGroups);
	}

}
