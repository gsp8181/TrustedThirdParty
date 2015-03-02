package com.team2.jax.contract;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR); implements Serializable {
	
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
