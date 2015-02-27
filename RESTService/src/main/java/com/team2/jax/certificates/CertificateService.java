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
	
	
	private static CertificateRepository crud = new CertificateRepositoryDynamo();
	
	private static CertificateValidator validator = new CertificateValidator();

	public Certificate findByEmail(String email) {
		return crud.findByEmail(email);
	}

	public Certificate create(CertificateIn cert) throws ConstraintViolationException, ValidationException, Exception {
		
		validator.validateCertificate(cert);	
		
		Certificate newCert = new Certificate();
		newCert.setEmail(cert.getEmail());
		newCert.setPublicKey(cert.getPublicKey());
		return crud.create(newCert);
		
	}
	
}
