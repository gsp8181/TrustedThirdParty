package com.team2.jax.certificates;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
//@Table(name = "certificate")
public class Certificate implements Serializable {

	//Default uid
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;

    //@Column(unique=true, nullable=false)
    private String username;
    
    //@Column(unique=true, nullable=false)
    private String publicKey;

    // If you are wondering, lob defines a data type that is large, may not need to be used though here
    //@Lob
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
