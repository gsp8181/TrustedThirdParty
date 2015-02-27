package com.team2.jax.certificates;

import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.team2.security.CertificateTools;

public class CertificateValidator {

	private static CertificateRepository cr = new CertificateRepositoryDynamo();
	
	private static Validator validator;
	
	public void validateCertificate(CertificateIn cert) {
		/*Set<ConstraintViolation<Certificate>> violations = validator.validate(cert);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }*/ //TODO: Broke
        
        if (certAlreadyExists(cert)) {
            throw new ValidationException("username:Username Already Exists");
        }
        
        if(!verifyCert(cert))
        {
        	throw new ValidationException("signedData:Certificate verification failed");
        }
        
        //Can cert user recieve emails from SNS?
        
        // If yes continue
        
        //If not send verification email AND fail this and discard all data
        
        //verifyEmail(String cert.getUsername());
	}

	private boolean certAlreadyExists(CertificateIn newCert) {
		Certificate oldCert=cr.findByEmail(newCert.getEmail());
		if(oldCert==null||!oldCert.getPublicKey().equals(newCert.getPublicKey())){return false;}
		return true;
	}
	
	private boolean verifyCert(CertificateIn cert)
	{
		try {
			return CertificateTools.verify(CertificateTools.decodeDSAPub(cert.getPublicKey()), cert.getEmail(), cert.getSignedData());
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

}

