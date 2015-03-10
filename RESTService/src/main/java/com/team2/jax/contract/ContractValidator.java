package com.team2.jax.contract;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryDynamo;
import com.team2.jax.contract.objects.ContractComplete;
import com.team2.jax.contract.objects.ContractStart;
import com.team2.security.CertificateTools;

public class ContractValidator {

	private CertificateRepository cs = new CertificateRepositoryDynamo();

	 private static Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
	
	public void validate(ContractStart ssObj) {
		Certificate cert = cs.findByEmail(ssObj.getEmail()); //TODO: if intermediate is NOT already in the database
		
		Set<ConstraintViolation<ContractStart>> violations = validator.validate(ssObj);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
		
		if(cert == null)
			throw new ValidationException("username:No certificate was found for the designated sender");
		
		if(cs.findByEmail(ssObj.getRecipient()) == null)
				throw new ValidationException("recipient:No certificate was found for the designated recipient, please check the name or tell them to register with the service");
		try {
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
			
			if(!CertificateTools.verify(ssPublicKey, ssObj.getDocData(), ssObj.getSig()))
				throw new ValidationException("sig:Validation of the signature failed, make sure the signing key is the database");
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
	}

	public void validateComplete(ContractComplete completeContract, Contract contract) {
		if(contract == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		if(contract.isCompleted())
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		
		Certificate cert = cs.findByEmail(contract.getRecipient());
		
		if(cert == null)
			throw new ValidationException("certificate:No certificate was found for the designated recipient");
		try {
		PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey()); 
		
		if(!CertificateTools.verify(ssPublicKey, contract.getIntermediateContract(), completeContract.getSig()))
			throw new ValidationException("certificate:Validation of the signature failed, make sure the signing key is the database");
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
	}

	public void validateDocRequest(String id,long timestamp, String signedId, Contract c) {
		
		if (c == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); //TODO: entity
		
		if(!c.isCompleted())
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		Certificate cert = cs.findByEmail(c.getRecipient());
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		
		try {
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
			if(!CertificateTools.verifyTimestamp(ssPublicKey, timestamp, signedId))
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
		
	}

	public void validateContractRequest(String id,long timestamp, String signedId, Contract c) {
		if (c == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); //TODO: entity
		
		if(!c.isCompleted())
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		Certificate cert = cs.findByEmail(c.getSender());
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		try {
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
			if(!CertificateTools.verifyTimestamp(ssPublicKey, timestamp, signedId))
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
	}

	public void validateIntRequest(String recipient, long timestamp, String signedId) {
		
		Certificate cert = cs.findByEmail(recipient);
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		try {
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
			if(!CertificateTools.verifyTimestamp(ssPublicKey, timestamp, signedId))
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}

		
	}

	public void validateAbortRequest(String id, long ts, String signedStamp,
			Contract c) {
		
		if (c == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		
		Certificate cert = cs.findByEmail(c.getSender());
		
		if(cert == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		
		try {
			PublicKey ssPublicKey = CertificateTools.decodeDSAPub(cert.getPublicKey());
			if(!CertificateTools.verifyTimestamp(ssPublicKey, ts, signedStamp))
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
		
		if(c.isCompleted())
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		
	}
	
	

}
