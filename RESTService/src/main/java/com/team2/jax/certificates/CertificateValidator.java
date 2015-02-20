package com.team2.jax.certificates;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

public class CertificateValidator {

	private static CertificateRepository cr = new CertificateRepositoryMemory();
	
	private static Validator validator;
	
	public void validateCertificate(Certificate cert) {
		/*Set<ConstraintViolation<Certificate>> violations = validator.validate(cert);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }*/ //TODO: Broke
        
        if (certAlreadyExists(cert.getUsername())) {
            throw new ValidationException("Username Already Exists");
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

}
