package com.team2.jax.certificates;

import java.util.Date;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.team2.jax.ses.EmailNotifier;

public class CertificateService {	
	
	private static CertificateRepository crud = new CertificateRepositoryDynamo();
	
	private static CertificateValidator validator = new CertificateValidator();
	
	private static EmailNotifier emailNotifier = EmailNotifier.getInstance();

	public Certificate findByEmail(String email) {
		return crud.findByEmail(email);
	}

	public Certificate create(CertificateIn cert) throws ConstraintViolationException, ValidationException {
		
		validator.validateCertificate(cert);	
		
		Certificate newCert = new Certificate();
		String code = UUID.randomUUID().toString();
		newCert.setEmail(cert.getEmail());
		newCert.setPublicKey(cert.getPublicKey());
		newCert.setTime(new Date().getTime());
		newCert.setStatus(false);	
		newCert.setCode(code);		
		crud.create(newCert);
		
		
		//TODO: newCert.getCode() send validation email to guy //http://ttp.gsp8181.co.uk/rest/certificates/verify?email="newCert.getEmail()"&code="newCert.getCode()"
		try {
			emailNotifier.sendEmail("verification.noreply@gsp8181.co.uk",newCert.getEmail(), EmailNotifier.LINK_CONTEXT, code);
		} catch (Exception e) {
			crud.delete(newCert);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
		newCert.setCode("Certificate not verified, check your emails to verify this certificate");
		return newCert;
		
	}

	public boolean verify(String email, String code) {
		return crud.verify(email, code); 
	}
	
}
