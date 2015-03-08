package com.team2.jax.contract.objects;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;

@XmlRootElement
public class ContractStart implements Serializable {

	private static final long serialVersionUID = 3L;

	/*
	 * Base 64 encoded (UTF-8) document
	 */
	@NotNull
	private String docData;
	
	/*
	 * File name
	 */
	@NotNull
	private String docName;
	
	/*
	 * SigA(h(doc)) in base64 format
	 */
	@NotNull
	private String sig;
	
	@NotNull
    @Email(message = "The email address must be in the format of name@domain.com")
	private String email;
	
	@NotNull
    @Email(message = "The email address must be in the format of name@domain.com")
	private String recipient;


	public String getDocData() {
		return docData;
	}

	public void setDocData(String docData) {
		this.docData = docData;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
