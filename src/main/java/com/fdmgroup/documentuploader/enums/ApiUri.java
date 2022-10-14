package com.fdmgroup.documentuploader.enums;

/**
 * Enum containing the request uris for the REST api.
 *
 * @author Noah Anderson
 */
public enum ApiUri {
    USERS ("/users"),
    ACCOUNTS ("/accounts"),
    REGISTER ("/register"),
    DOCUMENTS ("/documents"),
    AUTH_GROUP ("/authGroup"),
    IS_ENABLED ("/isEnabled"),
    CONFIRM_TOKEN ("/confirm-token"),
    RESET_PASSWORD ("/reset-password"),
    TOKEN ("/token");

    private final String uri;

    ApiUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }
}
