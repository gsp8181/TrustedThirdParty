package com.team2.jax.contract;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryMemory;
import com.team2.jax.contract.input.StartSign;

public class ContractValidator {

	public CertificateRepository cs = new CertificateRepositoryMemory();
	
	public void validate(StartSign ssObj) {
		Certificate cert = cs.findByUsername(ssObj.getUsername());
		
		if(cert == null)
			throw new WebApplicationException("No certificate was found for the designated sender");
		
	}

}
