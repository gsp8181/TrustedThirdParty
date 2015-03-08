package com.team2.jax.contract.objects;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ContractDoc implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String docRef;

	public String getDocRef() {
		return docRef;
	}

	public void setDocRef(String docRef) {
		this.docRef = docRef;
	}

}
