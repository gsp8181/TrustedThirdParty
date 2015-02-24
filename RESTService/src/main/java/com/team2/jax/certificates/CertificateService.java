package com.team2.jax.certificates;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

//@Dependent TODO: figure out
public class CertificateService {

	
	//@Inject
    //private @Named("logger") Logger log;
	
	//@Inject
    //private CertificateRepositoryTest crud; TODO: figure out
	private static CertificateRepository crud = new CertificateRepositoryMemory();
	
	private static CertificateValidator validator = new CertificateValidator();

	public Certificate findByUsername(String username) {
		return crud.findByUsername(username);
	}

	public Certificate create(CertificateIn cert) throws ConstraintViolationException, ValidationException, Exception {
		validator.validateCertificate(cert);
		
		Certificate newCert = new Certificate();
		newCert.setUsername(cert.getUsername());
		newCert.setPublicKey(cert.getPublicKey());
		
		return crud.create(newCert);
		
	}
	
}
