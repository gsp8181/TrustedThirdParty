package com.team2.jax.certificates;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import com.team2.jax.contract.EmailNotifier;

public class CertificateService {
	
	
	private static CertificateRepository crud = new CertificateRepositoryDynamo();
	
	private static CertificateValidator validator = new CertificateValidator();

	public Certificate findByEmail(String email) {
		return crud.findByEmail(email);
	}

	public Certificate create(CertificateIn cert) throws ConstraintViolationException, ValidationException , Exception {
		
		validator.validateCertificate(cert);	
		
		Certificate newCert = new Certificate();
		String code = UUID.randomUUID().toString();
		newCert.setEmail(cert.getEmail());
		newCert.setPublicKey(cert.getPublicKey());
		newCert.setTime(new Date().getTime());
		newCert.setStatus(false);	
		newCert.setCode(code);		
		crud.create(newCert);
		newCert.setCode("Certificate not verified, check your emails to verify this certificate");
		
		//TODO: newCert.getCode() send validation email to guy //http://ttp.gsp8181.co.uk/rest/certificates/verify?email="newCert.getEmail()"&code="newCert.getCode()"
		EmailNotifier.getInstance().sendEmail("verification.noreply@gsp8181.co.uk",newCert.getEmail(), EmailNotifier.LINK_CONTEXT, code);
		return newCert;
		
	}

	public boolean verify(String email, String code) {
		return crud.verify(email, code); 
	}
	
}
