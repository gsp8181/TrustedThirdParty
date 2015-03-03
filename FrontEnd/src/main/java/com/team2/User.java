package com.team2;

public class User {
	
	private String name;
	private String email;
	private String document;
	private String PrivateKey;
	private String PublicKey;
	private int id;
	private int keyId;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getPrivateKey() {
		return PrivateKey;
	}
	public void setPrivateKey(String privateKey) {
		PrivateKey = privateKey;
	}
	public String getPublicKey() {
		return PublicKey;
	}
	public void setPublicKey(String publicKey) {
		PublicKey = publicKey;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getKeyId() {
		return keyId;
	}
	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	

}
