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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getDocRef() {
		return docRef;
	}

	public void setDocRef(String docRef) {
		this.docRef = docRef;
	}

	public String getIntermediateContract() {
		return intermediateContract;
	}

	public void setIntermediateContract(String intermediateContract) {
		this.intermediateContract = intermediateContract;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
}
