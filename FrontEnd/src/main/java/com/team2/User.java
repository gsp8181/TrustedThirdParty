package com.team2;

import com.team2.security.Sig;

public class User implements java.io.Serializable{	
	
	private Sig sig;
	private String signedDoc;
	
	
	public Sig getSig() {
		return sig;
	}
	public void setSig(Sig sig) {
		this.sig = sig;
	}
	public String getSignedDoc() {
		return signedDoc;
	}
	public void setSignedDoc(String signedDoc) {
		this.signedDoc = signedDoc;
	}

}
