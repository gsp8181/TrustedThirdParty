package com.team2.jax.contract.input;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StartSign implements Serializable {

	private static final long serialVersionUID = 3L;

	private byte[] doc;
	
	/*
	 * SigA(h(doc)) in base64 format
	 */
	private String sig;
	
	private String username;
	
	private String receipent;

	public byte[] getDoc() {
		return doc;
	}

	public void setDoc(byte[] doc) {
		this.doc = doc;
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

	public String getReceipent() {
		return receipent;
	}

	public void setReceipent(String receipent) {
		this.receipent = receipent;
	}
}