package com.team2.jax.certificates;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
public class CertificateService {

	
	@Inject
    private @Named("logger") Logger log;
	
	@Inject
    private CertificateRepositoryTest crud;

	public Certificate findByUsername(String username) {
		return crud.findByUsername(username);
	}
	
}
