package com.team2.jax.contract;

public interface ContractFileStore {

	/**
	 * <p>
	 * Saves a file to a storage instance and returns an identifier that can be used to later retrieve the download link.
	 * </p>
	 * @param fileName The name of the file and extension to save
	 * @param doc The contents of the file
	 * @return An identifier that can be used with getTempLink to generate a unique download link
	 */
	public String saveFile(String fileName, byte[] doc);
	
	/**
	 * <p>
	 * Retrieves a unique link that can be used to download a file temporarily
	 * </p>
	 * @param identifier The identifier required to download the file
	 * @return The unique link to download the document (which should expire after a set time)
	 */
	public String getTempLink(String identifier);

}