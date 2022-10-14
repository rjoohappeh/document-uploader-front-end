package com.fdmgroup.documentuploader.model.account;

import java.math.BigDecimal;

import com.fdmgroup.documentuploader.model.user.User;
import org.springframework.stereotype.Component;

import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;

/**
 * <p>
 * Utility class which performs the logic of calculated the monetary cost
 * of {@link Account} objects based on the number of {@link User} objects
 * on the associated account and the account's {@link ServiceLevel}.
 * </p>
 * 
 * @author Noah Anderson
 * @author Roy Coates
 */
@Component
public class AccountCostCalculator {

	/**
	 * Maximum number of users an account with a {@link ServiceLevel#UNLIMITED Unlimited} 
	 * ServiceLevel may have at any given time.
	 * 
	 * @see ServiceLevel
	 */
	private static final int UNLIMITED_THRESHOLD = 10;
	
	/**
	 * Maximum number of users an account with a {@link ServiceLevel#ENTERPRISE Enterprise} 
	 * ServiceLevel may have at any given time.
	 * 
	 * @see ServiceLevel
	 */
	private static final int ENTERPRISE_THRESHOLD = 20;
	private static final int VALUE_TO_MAKE_ENTERPRISE_CALC_SIMPLE = 181;
	
	/**
	 * Calculates and returns the monetary cost of the given {@link Account} object.
	 * 
	 * @param account {@link Account} object to calculate the monetary cost of
	 * @return the monetary cost of the account
	 */
	public BigDecimal calculateRate(Account account) {
		ServiceLevel serviceLevel = account.getServiceLevel();
		BigDecimal rate = serviceLevel.getPrice();
		
		int priceToAdd = getPriceToAdd(account, serviceLevel);
		rate = addToRate(rate, priceToAdd);
		
		return rate;
	}
	
	private int getPriceToAdd(Account account, ServiceLevel level) {
		int priceToAdd = 0;
		if (isUnlimited(level)) {
			priceToAdd = (account.getUsers().size() - 1) / UNLIMITED_THRESHOLD;
		}
		if (isEnterprise(level) ) {
			priceToAdd = (account.getUsers().size() - VALUE_TO_MAKE_ENTERPRISE_CALC_SIMPLE) / ENTERPRISE_THRESHOLD;
		}
		
		return priceToAdd;
	}
	
	private BigDecimal addToRate(BigDecimal rate, int priceToAdd) {
		if (priceToAdd > 0) {
			rate = rate.add(new BigDecimal(priceToAdd));
		}
		return rate;
	}
	
	private boolean isUnlimited(ServiceLevel level) {
		return level.equals(ServiceLevel.UNLIMITED);
	}
	
	private boolean isEnterprise(ServiceLevel level) {
		return level.equals(ServiceLevel.ENTERPRISE);
	}
}
