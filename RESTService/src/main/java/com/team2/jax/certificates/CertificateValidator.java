package com.team2.jax.certificates;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.team2.security.CertificateTools;

public class CertificateValidator {

	private static CertificateRepository cr = new CertificateRepositoryMemory();
	
	private static Validator validator;
	
	public void validateCertificate(CertificateIn cert) {
		/*Set<ConstraintViolation<Certificate>> violations = validator.validate(cert);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }*/ //TODO: Broke
        
        if (certAlreadyExists(cert.getUsername())) {
            throw new ValidationException("Username Already Exists");
        }
        
        if(!verifyCert(cert))
        {
        	throw new ValidationException("Certificate verification failed");
        }
	}

	private boolean certAlreadyExists(String username) {
		if(cr.findByUsername(username) != null)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	private boolean verifyCert(CertificateIn cert)
	{
		try {
			return CertificateTools.verify(CertificateTools.decodeDSAPub(cert.getPublicKey()), cert.getUsername(), cert.getSignedData());
		} catch (Exception e) {
			//e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

}
