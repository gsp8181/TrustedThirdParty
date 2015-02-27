package com.team2.jax.contract;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryDynamo;

import com.team2.jax.certificates.CertificateRepositoryMemory;


import com.team2.security.CertificateTools;

public class ContractValidator {

	private CertificateRepository cs = new CertificateRepositoryDynamo();

	
	public void validate(ContractStart ssObj) throws Exception { //TODO: all fields need to be in place
		Certificate cert = cs.findByEmail(ssObj.getEmail()); //TODO: if intermediate is NOT already in the database
		
		if(cert == null)
			throw new ValidationException("username:No certificate was found for the designated sender");
		
		if(cs.findByEmail(ssObj.getRecipient()) == null)
				throw new ValidationException("recipient:No certificate was found for the designated recipient, please check the name or tell them to register with the service");
		
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
			
			if(!CertificateTools.verify(ssPublicKey, ssObj.getDocData(), ssObj.getSig()))
				throw new ValidationException("sig:Validation of the signature failed, make sure the signing key is the database");
	}

	public void validateComplete(ContractComplete completeContract, Contract contract) throws Exception {
		if(contract.isCompleted())
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		
		Certificate cert = cs.findByEmail(contract.getRecipient());
		
		if(cert == null)
			throw new ValidationException("certificate:No certificate was found for the designated recipient");
		
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
		
		if(!CertificateTools.verify(ssPublicKey, contract.getIntermediateContract(), completeContract.getSig()))
			throw new ValidationException("certificate:Validation of the signature failed, make sure the signing key is the database");
	}

	public void validateDocRequest(String id, String signedId, Contract c) throws Exception {
		
		if (c == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		if(!c.isCompleted())
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		Certificate cert = cs.findByEmail(c.getRecipient());
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
		
		if(!CertificateTools.verify(ssPublicKey, id, signedId))
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
	}

	public void validateContractRequest(String id, String signedId, Contract c) throws Exception {
		if (c == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		if(!c.isCompleted())
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		Certificate cert = cs.findByEmail(c.getSender());
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
		
		if(!CertificateTools.verify(ssPublicKey, id, signedId))
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
	}
	
	

}
