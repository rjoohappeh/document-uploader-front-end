package com.fdmgroup.documentuploader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Encapsulates all constant values to be used throughout the application which
 * are located in any of the {@code .properties} files within the project
 * hierarchy.
 *
 * @author Noah Anderson
 *
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

	private final RequestUris requestUris;

	public ApplicationProperties(RequestUris requestUris) {
		super();
		this.requestUris = requestUris;
	}

	public RequestUris getRequestUris() {
		return requestUris;
	}

	/**
	 * Static inner class of {@link ApplicationProperties} which encapsulates all
	 * the request uris used throughout the application.
	 *
	 * @author Noah Anderson
	 *
	 */
	public static class RequestUris {

		private final String login;
		private final String dashboard;
		private final String account;
		private final String settings;
		private final String editProfileInfo;
		private final String updatePassword;
		private final String serviceLevel;
		private final String upgradeServiceLevel;
		private final String addUser;
		private final String deleteUser;
		private final String forgotPassword;
		private final String resetPassword;
		private final String deleteDocument;
		private final String downloadDocument;
		private final String confirmAccount;
		private final String changePassword;

		public RequestUris(String login, String dashboard, String account, String settings, String editProfileInfo,
				String updatePassword, String serviceLevel, String upgradeServiceLevel, String addUser,
				String deleteUser, String forgotPassword, String resetPassword, String deleteDocument,
				String downloadDocument, String confirmAccount, String changePassword) {
			super();
			this.login = login;
			this.dashboard = dashboard;
			this.account = account;
			this.settings = settings;
			this.editProfileInfo = editProfileInfo;
			this.updatePassword = updatePassword;
			this.serviceLevel = serviceLevel;
			this.upgradeServiceLevel = upgradeServiceLevel;
			this.addUser = addUser;
			this.deleteUser = deleteUser;
			this.forgotPassword = forgotPassword;
			this.resetPassword = resetPassword;
			this.deleteDocument = deleteDocument;
			this.downloadDocument = downloadDocument;
			this.confirmAccount = confirmAccount;
			this.changePassword = changePassword;
		}

		public String getLogin() {
			return login;
		}

		public String getDashboard() {
			return dashboard;
		}

		public String getAccount() {
			return account;
		}

		public String getSettings() {
			return settings;
		}

		public String getEditProfileInfo() {
			return editProfileInfo;
		}

		public String getUpdatePassword() {
			return updatePassword;
		}

		public String getServiceLevel() {
			return serviceLevel;
		}

		public String getUpgradeServiceLevel() {

			return upgradeServiceLevel;
		}

		public String getAddUser() {
			return addUser;
		}

		public String getDeleteUser() {
			return deleteUser;
		}

		public String getForgotPassword() {
			return forgotPassword;
		}

		public String getResetPassword() {
			return resetPassword;
		}

		public String getDeleteDocument() {
			return deleteDocument;
		}

		public String getDownloadDocument() {
			return downloadDocument;
		}

		public String getConfirmAccount() {
			return confirmAccount;
		}

		public String getChangePassword() {
			return changePassword;
		}
	}
}
