package com.team2.jax.certificates;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

public class CertificateService {
	
	
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
		newCert.setTime(new Date().getTime());
		newCert.setStatus(false);	
		newCert.setCode(UUID.randomUUID().toString());
		Certificate out = crud.create(newCert);
		out.setCode("Certificate not verified, check your emails to verify this certificate");
		return out;
		
	}

	public boolean verify(String email, String code) {
		return crud.verify(email, code);
	}
	
}
