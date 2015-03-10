package com.team2.jax.certificates;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.team2.security.CertificateTools;

public class CertificateValidator {

	private static CertificateRepository cr = new CertificateRepositoryDynamo();
	
	 private static Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
	
	public void validateCertificate(CertificateIn cert) {
		Set<ConstraintViolation<CertificateIn>> violations = validator.validate(cert);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        
        if (certAlreadyExists(cert)) {
            throw new ValidationException("username:Username Already Exists");
        }
        
        if(!verifyCert(cert))
        {
        	throw new ValidationException("signedData:Certificate verification failed");
        }
        
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

