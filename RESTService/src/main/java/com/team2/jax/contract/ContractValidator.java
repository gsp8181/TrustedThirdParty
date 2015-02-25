package com.team2.jax.contract;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryMemory;
import com.team2.jax.contract.input.Complete;
import com.team2.jax.contract.input.StartSign;
import com.team2.security.CertificateTools;

public class ContractValidator {

	public CertificateRepository cs = new CertificateRepositoryMemory();
	
	public void validate(StartSign ssObj) throws Exception {
		Certificate cert = cs.findByUsername(ssObj.getUsername()); //TODO: if intermediate is NOT already in the database
		
		if(cert == null)
			throw new ValidationException("No certificate was found for the designated sender");
		
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
			
			if(!CertificateTools.verify(ssPublicKey, ssObj.getDoc(), ssObj.getSig())) //TODO: better signing error
				throw new ValidationException("Validation of the signature failed, make sure the signing key is the database");
	}

	public void validateComplete(Complete completeContract, Contract contract) throws Exception {
		Certificate cert = cs.findByUsername(contract.getRecipient()); //TODO: If the contract is already signed throw and error
		
		if(cert == null)
			throw new ValidationException("certificate:No certificate was found for the designated recipient"); //TODO: spelling
		
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
		
		if(!CertificateTools.verify(ssPublicKey, contract.getIntermediateContract(), completeContract.getSig())) //TODO: better signing error
			throw new ValidationException("Validation of the signature failed, make sure the signing key is the database");
	}

}
