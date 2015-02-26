package com.team2.jax.contract;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContractIntermediate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String sender;
	
	private String recipient;
	
	private String sigSender;
	
	private String id;
	
    private String docName;

	public String getUsername() {
		return sender;
	}

	public void setUsername(String username) {
		this.sender = username;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getSigSender() {
		return sigSender;
	}

	public void setSigSender(String sigSender) {
		this.sigSender = sigSender;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}
}
