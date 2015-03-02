package com.team2.jax.contract;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ContractFileStoreS3Test {
	
	static ContractFileStoreS3 test = new ContractFileStoreS3();
	
	public static void main (String [] args) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		byte[] doc = "0000".getBytes();
		
		String fileName = "fileName";
		String identifier = test.saveFile(fileName, doc);
		System.out.println("Temporary URL: "+test.getTempLink(identifier));
	}
}
