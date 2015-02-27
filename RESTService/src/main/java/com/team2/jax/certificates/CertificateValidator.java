package com.team2.jax.certificates;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;


public class CertificateValidator {

	private static CertificateRepository cr = new CertificateRepositoryDynamo();
	
//	private static Validator validator;
	
	public void validateCertificate(Certificate cert) {
//		Set<ConstraintViolation<Certificate>> violations = validator.validate(cert);
//
//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
//        }
        
        if (certAlreadyExists(cert)) {
            throw new ValidationException("Certificate Already Exists:"+cert.getEmail() + " " + cert.getPublicKey());
        }
        
        
	}

	private boolean certAlreadyExists(Certificate newCert) {
		Certificate oldCert=cr.findByEmail(newCert.getEmail());
		if(oldCert==null||!oldCert.getPublicKey().equals(newCert.getPublicKey())){return false;}
		return true;
		
	}
	
	

}
