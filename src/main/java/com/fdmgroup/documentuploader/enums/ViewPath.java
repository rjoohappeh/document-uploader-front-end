package com.fdmgroup.documentuploader.enums;

/**
 * Enum containing the view paths of each template returned from Controllers.
 *
 * @author Noah Anderson
 */
public enum ViewPath {
    DASHBOARD ("users/dashboard"),
    ACCOUNT ("users/account"),
    EDIT_PROFILE_INFO ("users/editProfileInformation"),
    SERVICE_LEVEL_INFO ("users/serviceLevelInformation"),
    SETTINGS ("users/settings"),
    CONFIRM_TOKEN ("confirmtoken"),
    REGISTRATION ("registration"),
    LOGIN ("login"),
    ERROR_403 ("error403"),
    FORGOT_PASSWORD ("forgotPassword"),
    CHANGE_PASSWORD ("changePassword");

    private final String path;

    ViewPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
