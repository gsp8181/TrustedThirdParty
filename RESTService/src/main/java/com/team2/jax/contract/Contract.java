package com.team2.jax.contract;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Contract implements Serializable {

	//Default uid
	private static final long serialVersionUID = 2L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;
    
    private String sender;
    
    private String recipient;
    
    private String docRef;
    
	/*
	 * SigA(h(doc)) in base64 format
	 */
    private String intermediateContract;
    
	/*
	 * SigB(SigA(h(doc))) in base64 format
	 */
    private String contract;
    
    private boolean completed;
	
}
