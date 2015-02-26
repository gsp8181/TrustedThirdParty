package com.team2.jax.contract;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContractComplete implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String sig;

	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}
}