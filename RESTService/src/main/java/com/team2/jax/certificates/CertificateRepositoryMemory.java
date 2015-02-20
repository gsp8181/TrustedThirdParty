package com.team2.jax.certificates;

import java.util.HashMap;
import java.util.Map;

public class CertificateRepositoryMemory implements CertificateRepository {

	private static Map<String, Certificate> repo = new HashMap<String, Certificate>();

	public Certificate findByUsername(String username) {
		if (repo.containsKey(username)) {
			return repo.get(username);
		} else {
			return null;
		}
	}

	public Certificate create(Certificate certificate) {
		if (repo.containsKey(certificate.getUsername())) {
			return null; // todo: should be not created
		} else {
			return repo.put(certificate.getUsername(),certificate);
		}
	}

}
