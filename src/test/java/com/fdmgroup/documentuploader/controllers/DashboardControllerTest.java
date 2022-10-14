package com.fdmgroup.documentuploader.controllers;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.user.AuthGroup;
import com.fdmgroup.documentuploader.model.user.Role;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.security.AppUserPrincipal;
import com.fdmgroup.documentuploader.service.account.AccountService;
import com.fdmgroup.documentuploader.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DashboardControllerTest {

	@Mock
	private User mockUser;
	
	@Mock
	private Account mockAccount;
	
	@MockBean
	private UserService mockUserService;
	
	@MockBean
	private AccountService mockAccountService;
	
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@WithMockUser(roles = "USER")
	void testReturnToDashBoard_addsUserAndUserAccountsToModel() throws Exception {
		when(mockUserService.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(mockAccountService.getAllAccountsByUserId(anyLong())).thenReturn(Collections.emptyList());
		
		List<AuthGroup> authGroups = new ArrayList<>();
		authGroups.add(new AuthGroup("", Role.ROLE_USER));
		
		mockMvc.perform(get("/dashboard")
							.sessionAttr("user", mockUser)
							.with(user(new AppUserPrincipal(mockUserService, mockUser, authGroups))))
							.andExpect(matchAll(
									status().isOk(),
									model().attribute("user", mockUser),
									model().attribute("accounts", Collections.emptyList())));

	}
}
