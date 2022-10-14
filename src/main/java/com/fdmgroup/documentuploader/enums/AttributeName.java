package com.fdmgroup.documentuploader.enums;

/**
 * Enum containing the model attribute names used throughout the controllers.
 *
 * @author Noah Anderson
 */
public enum AttributeName {
	ACCOUNT ("account"),
    ACCOUNTS ("accounts"),
    MESSAGE ("message"),
    USER ("user"),
    DOCUMENT_DELETED ("documentDeleted"),
    LOGIN_DTO ("loginDto"),
    LEVELS ("levels"),
    REGISTER_DTO ("registerDto"),
    EMAIL ("email"),
    REGISTER_SUCCESS ("registerSuccess"),
    CONFIRM_TOKEN_SUCCESS ("confirmTokenSuccess"),
    CONFIRM_TOKEN_FAILURE ("confirmTokenFailure"),
    PRICE ("price"),
    ROLE ("role"),
    TOKEN ("token"),
    RESET_PASSWORD_DTO ("resetPasswordDto");

	private String value;

	AttributeName(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
