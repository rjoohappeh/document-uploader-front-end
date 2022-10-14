package com.fdmgroup.documentuploader.model.document;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Encapsulates information related to a document/file uploaded from a client to
 * the database.
 * 
 * @author Noah Anderson
 * @author Roy Coates
 *
 */
@Component
@Scope("prototype")
public class Document {

	private long id;
	
	/**
	 * The contents of the document.
	 */
	private byte[] content;
	
	/**
	 * The name of the document.
	 */
	private String name;
	
	/**
	 * The document type (e.g., docx, csv, txt, jpg, etc.)
	 */
	private String extension;

	public Document() {
	}

	public Document(String name, String extension, byte[] content) {
		this.name = name;
		this.extension = extension;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	@Override
	public String toString() {
		return "Document [id=" + id + ", content=" + Arrays.toString(content) + ", name=" + name + ", extension="
				+ extension + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(content);
		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Document other = (Document) obj;
		if (!Arrays.equals(content, other.content))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
}
