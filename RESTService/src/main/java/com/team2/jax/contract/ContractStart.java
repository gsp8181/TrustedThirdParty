package com.team2.jax.contract;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContractStart implements Serializable {

	private static final long serialVersionUID = 3L;

	/*
	 * Base 64 encoded (UTF-8) document
	 */
	private String docData;
	
	/*
	 * File name
	 */
	private String docName;
	
	/*
	 * SigA(h(doc)) in base64 format
	 */
	private String sig;
	
	private String username;
	
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
