package com.fdmgroup.documentuploader.account;

import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.account.AccountCostCalculator;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountCostCalculatorTest {

	@Autowired
	private ApplicationContext ctx;

	private AccountCostCalculator calculator;

	@Mock
	private Account mockAccount;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		calculator = new AccountCostCalculator();
	}

	@Test
	void returnZeroWhenLevelIsBronze() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.BRONZE);
		assertEquals(BigDecimal.ZERO, calculator.calculateRate(mockAccount));
	}

	@Test
	void returnOneWhenLevelIsSilver() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.SILVER);
		assertEquals(BigDecimal.ONE, calculator.calculateRate(mockAccount));
	}

	@Test
	void returnTwoWhenLevelIsGold() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.GOLD);
		assertEquals(new BigDecimal("2"), calculator.calculateRate(mockAccount));
	}

	@Test
	void returnFiveWhenLevelIsUnlimitedAndNumberOfUsersIsTenOrLess() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.UNLIMITED);
		assertEquals(new BigDecimal("5"), calculator.calculateRate(mockAccount));
	}

	@Test
	void returnSixWhenLevelIsUnlimitedAndNumberOfUsersIsBetweenTenAndEleven() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.UNLIMITED);
		Set<User> users = new HashSet<>();
		for(int i=0; i < 11; i++) {
			User user = ctx.getBean("user", User.class);
			user.setEmail("email"+i+"@email.com");
			users.add(user);
		}
		when(mockAccount.getUsers()).thenReturn(users);
		assertEquals(new BigDecimal("6"), calculator.calculateRate(mockAccount));
	}
	
	@Test
	void returnFifteenWhenLevelIsEnterpriseAndUsersIsLessThanTwoHundred() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.ENTERPRISE);
		assertEquals(new BigDecimal("15"), calculator.calculateRate(mockAccount));
	}
	
	@Test
	void returnSixteenWhenLevelIsEnterpriseAndUsersIsLessOrEqualToTwoHundredTwentyAndGreaterThanTwoHundred() {
		when(mockAccount.getServiceLevel()).thenReturn(ServiceLevel.ENTERPRISE);
		Set<User> users = new HashSet<>();
		for(int i=0; i < 201; i++) {
			User user = ctx.getBean("user", User.class);
			user.setEmail("email"+i+"@email.com");
			users.add(user);
		}
		when(mockAccount.getUsers()).thenReturn(users);
		assertEquals(new BigDecimal("16"), calculator.calculateRate(mockAccount));
	}

}
