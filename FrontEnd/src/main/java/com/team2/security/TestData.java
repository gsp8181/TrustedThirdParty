package com.team2.security;

public class TestData {

	public TestData(String publicKeyBase64, String signedData,
			String sigBase64, String privateKeyBase64) {
		this.publicKeyBase64 = publicKeyBase64;
		this.signedData = signedData;
		this.sigBase64 = sigBase64;
		this.privateKeyBase64 = privateKeyBase64;
	}

	public String publicKeyBase64;
	public String signedData;
	public String sigBase64;
	public String privateKeyBase64;

	public void print() {
		System.out.println(publicKeyBase64);
		System.out.println(signedData);
		System.out.println(sigBase64);
		System.out.println(privateKeyBase64);
	}
}
