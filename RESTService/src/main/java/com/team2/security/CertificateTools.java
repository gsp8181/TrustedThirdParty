package com.team2.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.logging.Logger;

public class CertificateTools {

	private static Logger log = Logger.getAnonymousLogger();
	
	/*public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, InvalidKeySpecException {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		KeyPair pair = keyGen.generateKeyPair();
		
		Signature dsa = Signature.getInstance("SHA1withDSA");
		String dataS = "signed";
		byte[] data = dataS.getBytes();
		
		PrivateKey priv = pair.getPrivate();
		dsa.initSign(priv);
		dsa.update(data);
		byte[] sig = dsa.sign();
		
		PublicKey pub = pair.getPublic();
		
		PublicKey pub2;
		
			pub2 = decode(encoded(pub));
		
		dsa.initVerify(pub2);

		System.out.println(encoded(pub));
		
		dsa.update(data);
		boolean verifies = dsa.verify(sig);
		System.out.println("signature verifies: " + verifies);
	}*/
	
	public static TestData getTestData(String dataToSign) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		KeyPair pair = keyGen.generateKeyPair();
		
		Signature dsa = Signature.getInstance("SHA1withDSA");
		
		PrivateKey priv = pair.getPrivate();
		dsa.initSign(priv);
		dsa.update(dataToSign.getBytes());
		byte[] sig = dsa.sign();
		
		PublicKey pub = pair.getPublic();
		
		TestData out = new TestData(encodeDSA(pub), dataToSign, encodeBase64(sig), encodeDSA(priv));
		
		return out;
	}
	
	
	public static String encodeDSA(PrivateKey key)
	{
		byte[] array = key.getEncoded();
		Encoder encoder = Base64.getEncoder();
		byte[] out = encoder.encode(array);
		return new String(out);
	}
	
	public static String encodeDSA(PublicKey key)
	{
		byte[] array = key.getEncoded();
		Encoder encoder = Base64.getEncoder();
		byte[] out = encoder.encode(array);
		return new String(out);
	}

	public static PublicKey decodeDSAPub(String key) throws NoSuchAlgorithmException, InvalidKeySpecException{
	        byte[] byteKey = decodeBase64(key);
	        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
	        KeyFactory kf = KeyFactory.getInstance("DSA");

	        return kf.generatePublic(X509publicKey);
	}
	
	public static byte[] decodeBase64(String value)
	{
    	Decoder decoder = Base64.getDecoder();
        byte[] byteKey = decoder.decode(value.getBytes());
        return byteKey;
	}
	
	public static String encodeBase64(byte[] bytes)
	{
		Encoder encoder = Base64.getEncoder();
		byte[] out = encoder.encode(bytes);
		return new String(out);
	}
	
	public static String encodeBase64(String string)
	{
		return encodeBase64(string.getBytes());
	}
	
	public static boolean verify(String keyBase64, String signedData, String sigBase64) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException
	{
		log.info("verifying " + keyBase64 + " : " + signedData + " : " + sigBase64);
		return verify(decodeDSAPub(keyBase64), signedData, sigBase64);
	}
	
	public static boolean verify(PublicKey key, String signedData, String sigBase64) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		Signature dsa = Signature.getInstance("SHA1withDSA");
		dsa.initVerify(key);
		dsa.update(signedData.getBytes());
		return dsa.verify(decodeBase64(sigBase64));
	}
	
}
