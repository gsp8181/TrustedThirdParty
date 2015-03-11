package com.team2.security;

public class Sig implements java.io.Serializable{

	public Sig(String publicKeyBase64, String signedData,
			String sigBase64, String privateKeyBase64) {
		this.publicKeyBase64 = publicKeyBase64;
		this.signedData = signedData;
		this.sigBase64 = sigBase64;
		this.privateKeyBase64 = privateKeyBase64;
	}

	private String publicKeyBase64;
	private String signedData;
	private String sigBase64;
	private String privateKeyBase64;

	public String getPublicKeyBase64() {
		return publicKeyBase64;
	}

	public void setPublicKeyBase64(String publicKeyBase64) {
		this.publicKeyBase64 = publicKeyBase64;
	}

	public String getSignedData() {
		return signedData;
	}

	public void setSignedData(String signedData) {
		this.signedData = signedData;
	}

	public String getSigBase64() {
		return sigBase64;
	}

	public void setSigBase64(String sigBase64) {
		this.sigBase64 = sigBase64;
	}

	public String getPrivateKeyBase64() {
		return privateKeyBase64;
	}

	public void setPrivateKeyBase64(String privateKeyBase64) {
		this.privateKeyBase64 = privateKeyBase64;
	}

	public void print() {
		System.out.println(publicKeyBase64);
		System.out.println(signedData);
		System.out.println(sigBase64);
		System.out.println(privateKeyBase64);
	}
}
