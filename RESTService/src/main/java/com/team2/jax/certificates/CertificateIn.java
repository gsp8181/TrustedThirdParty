package com.team2.jax.certificates;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CertificateIn implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotNull
    private String email;
    
    /**
     * The public key is the DSA public key in base64 format
     */
	@NotNull
    private String publicKey;
    
    /**
     * The signed data is the DSA signed username in base64 format
     */
	@NotNull
    private String signedData;

	public String getSignedData() {
		return signedData;
	}

	public void setSignedData(String signedData) {
		this.signedData = signedData;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
