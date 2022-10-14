package com.fdmgroup.documentuploader.model.account;

import java.util.Set;

import com.fdmgroup.documentuploader.dto.validation.annotation.UniqueAccountName;
import com.fdmgroup.documentuploader.model.account.servicelevel.ServiceLevel;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Encapsulates information related to a user/users' account.
 * 
 * @author Noah Anderson
 * @author Roy Coates
 * @see User
 */
@Component
@Scope("prototype")
public class Account {

	private long id;
	@UniqueAccountName
	private String name;
	private User owner;
	private ServiceLevel serviceLevel;
	private Set<User> users;
	private Set<Document> documents;

	public Account() {
		super();
	}

	public Account(AccountBuilder builder) {
		this.name = builder.name;
		this.owner = builder.owner;
		this.serviceLevel = builder.serviceLevel;
		this.users = builder.users;
		this.documents = builder.documents;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public ServiceLevel getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(ServiceLevel serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	public boolean hasMaxUsers() {
		int maxUsers = serviceLevel.getMaxUsers();
		if (serviceLevel == ServiceLevel.UNLIMITED || serviceLevel == ServiceLevel.ENTERPRISE) {
			return false;
		}
		return users.size() >= maxUsers;
	}

	public static class AccountBuilder {

		private ServiceLevel serviceLevel;
		private Set<User> users;
		private String name;
		private User owner;
		private Set<Document> documents;

		public AccountBuilder setServiceLevel(ServiceLevel serviceLevel) {
			this.serviceLevel = serviceLevel;
			return this;
		}

		public AccountBuilder setUsers(Set<User> users) {
			this.users = users;
			return this;
		}

		public AccountBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public AccountBuilder setOwner(User owner) {
			this.owner = owner;
			return this;
		}

		public AccountBuilder setDocuments(Set<Document> documents) {
			this.documents = documents;
			return this;
		}

		public Account build() {
			return new Account(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((serviceLevel == null) ? 0 : serviceLevel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (serviceLevel == null) {
			if (other.serviceLevel != null)
				return false;
		} else if (!serviceLevel.equals(other.serviceLevel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", documents=" + documents + "]";
	}

}
