package com.team2.jax.contract;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryMemory;
import com.team2.security.CertificateTools;

public class ContractValidator {

	private CertificateRepository cs = new CertificateRepositoryMemory();

	
	public void validate(ContractStart ssObj) throws Exception {
		Certificate cert = cs.findByUsername(ssObj.getUsername()); //TODO: if intermediate is NOT already in the database
		
		if(cert == null)
			throw new ValidationException("No certificate was found for the designated sender");
		
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
			
			if(!CertificateTools.verify(ssPublicKey, ssObj.getDoc(), ssObj.getSig())) //TODO: better signing error
				throw new ValidationException("Validation of the signature failed, make sure the signing key is the database");
	}

	public void validateComplete(ContractComplete completeContract, Contract contract) throws Exception {
		Certificate cert = cs.findByUsername(contract.getRecipient()); //TODO: If the contract is already signed throw and error
		
		if(cert == null)
			throw new ValidationException("certificate:No certificate was found for the designated recipient");
		
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
		
		if(!CertificateTools.verify(ssPublicKey, contract.getIntermediateContract(), completeContract.getSig())) //TODO: better signing error
			throw new ValidationException("certificate:Validation of the signature failed, make sure the signing key is the database");
	}

	public void validateDocRequest(String id, String signedId, Contract c) throws Exception {
		
		if (c == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		if(!c.isCompleted())
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		Certificate cert = cs.findByUsername(c.getRecipient());
		
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
		
		Certificate cert = cs.findByUsername(c.getSender());
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
		
		if(!CertificateTools.verify(ssPublicKey, id, signedId))
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
	}
	
	

}
