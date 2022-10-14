package com.fdmgroup.documentuploader.model.account.servicelevel;

import com.fdmgroup.documentuploader.model.account.Account;

import java.math.BigDecimal;

/**
 * Encapsulates all constant data related to the service level of an {@linkplain Account}.
 * 
 * @author Noah Anderson
 * @author Roy Coates
 * @see Account
 */
public enum ServiceLevel {

	BRONZE("Bronze", BigDecimal.ZERO, 2, 2, 1, true), 
	SILVER("Silver", BigDecimal.ONE, 5, 10, 1, true), 
	GOLD("Gold", new BigDecimal("2"), 20, 50, 2, false), 
	UNLIMITED("Unlimited", new BigDecimal("5"), -1, -1, 10, false), 
	ENTERPRISE("Enterprise", new BigDecimal("15"), -1, -1, 200, false);

	private final String name;
	private final BigDecimal price;
	private final int maxUploads;
	private final int maxUploadsPerMonth;
	private final int maxUsers;
	private final boolean ads;

	ServiceLevel(String name, BigDecimal price, int maxUploads, int maxUploadsPerMonth, int maxUsers,
			boolean ads) {
		this.name = name;
		this.price = price;
		this.maxUploads = maxUploads;
		this.maxUploadsPerMonth = maxUploadsPerMonth;
		this.maxUsers = maxUsers;
		this.ads = ads;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public int getMaxUploads() {
		return maxUploads;
	}

	public int getMaxUploadsPerMonth() {
		return maxUploadsPerMonth;
	}

	public int getMaxUsers() {
		return maxUsers;
	}

	public boolean isAds() {
		return ads;
	}

	@Override
	public String toString() {
		return name;
	}
}
