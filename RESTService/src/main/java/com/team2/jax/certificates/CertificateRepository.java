package com.team2.jax.certificates;

public interface CertificateRepository {

	public Certificate findByUsername(String username);

	public Certificate create(Certificate certificate);
}
