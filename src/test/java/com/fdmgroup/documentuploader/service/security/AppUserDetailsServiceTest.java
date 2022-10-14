package com.fdmgroup.documentuploader.service.security;


import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.security.AppUserPrincipal;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppUserDetailsServiceTest {

    private static final String TEST_USERNAME = "testUsername";

    private AppUserDetailsService appUserDetailsService;
    @Mock
    private UserService mockUserService;
    @Mock
    private AuthGroupService mockAuthGroupService;
    @Mock
    private User mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        this.appUserDetailsService = new AppUserDetailsService(mockUserService, mockAuthGroupService);
    }

    @Test
    void testLoadUserByUsername_callsUserServiceFindByEmail() {
        when(mockUserService.findByEmail(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        appUserDetailsService.loadUserByUsername(TEST_USERNAME);

        verify(mockUserService).findByEmail(TEST_USERNAME);
    }

    @Test
    void testLoadUserByUsername_throwsUsernameNotFoundException_whenUsernameNotFound() {
        when(mockUserService.findByEmail(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> appUserDetailsService.loadUserByUsername(TEST_USERNAME));
    }

    @Test
    void testLoadByUsername_doesNotThrowUsernameNotFoundException_whenUsernameIsNotFound() {
        when(mockUserService.findByEmail(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        Assertions.assertDoesNotThrow(() -> appUserDetailsService.loadUserByUsername(TEST_USERNAME));
    }

    @Test
    void testLoadUserByUsername_callsAuthGroupServiceFindByUsername() {
        when(mockUserService.findByEmail(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        appUserDetailsService.loadUserByUsername(TEST_USERNAME);

        verify(mockAuthGroupService).findByUsername(TEST_USERNAME);
    }

    @Test
    void testLoadByUsername_returnsUserDetailsWithUserAndAuthGroups() {
        when(mockUserService.findByEmail(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = appUserDetailsService.loadUserByUsername(TEST_USERNAME);

        UserDetails expected = new AppUserPrincipal(mockUserService, mockUser, Collections.emptyList());
        assertEquals(expected, userDetails);
    }
}
