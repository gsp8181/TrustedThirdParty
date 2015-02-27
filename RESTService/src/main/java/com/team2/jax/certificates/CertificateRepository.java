package com.team2.jax.certificates;

/**
 * <p>
 * Certificate Repository (interface)
 * </p>
 * <p>
 * An interface that specifies the required methods any database binding must
 * implement to be included in the system.
 * </p>
 * 
 * @author Geoffrey Prytherch <gsp8181@users.noreply.github.com>
 * @since 2015-02-18
 * @see Certificate
 *
 */
public interface CertificateRepository {

	/**
	 * <p>
	 * Retrieves the most recently added certificate object associated with the
	 * provided email address
	 * </p>
	 * 
	 * @param email
	 *            The email address associated with the certificate
	 * @return The Certificate object if found, else null
	 */
	public Certificate findByEmail(String email);

	/**
	 * <p>
	 * Creates a new certificate object in the repository
	 * </p>
	 * <p>
	 * The repository MUST accept any valid certificate unless it is a direct
	 * duplicate (same email and public key combination)
	 * </p>
	 * 
	 * @param certificate
	 *            The Certificate object to be added to the database
	 * @return The Certificate object's database representation (including the
	 *         id)
	 */
	public Certificate create(Certificate certificate);
}
