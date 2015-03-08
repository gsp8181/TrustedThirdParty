package com.team2.jax.contract.objects;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContractComplete implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String sig;

	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}
}
