package frontend;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64.*;

import com.team2.security.Encoder;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			System.out.println("Hi, i am front end.0");
			
			  /* Generate a DSA signature */

	        if (args.length != 1) {
	            System.out.println("Usage: GenSig username");
	        }
	        else try {
	        	generatingKeyTest();
	        // the rest of the code goes here

	        } catch (Exception e) {
	            System.err.println("Caught exception " + e.toString());
	        }
	}
	
	public static void generatingKeyTest() throws NoSuchAlgorithmException, NoSuchProviderException{
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		
		keyGen.initialize(1024, random);
		//generate key
		KeyPair pair = keyGen.generateKeyPair();
		//private key object
		PrivateKey priv = pair.getPrivate();
		//public key object
		PublicKey pub = pair.getPublic();
		
		String encodedKey = encodeDSA(pub);
		
		Signature dsa = Signature.getInstance("SHA1withDSA");
		
		dsa.initSign(priv);
		dsa.update(username.getBytes());
		String sig = encodeBase64(dsa.sign());
		
//		priv.getEncoded();
	}
	
	/**
	 * Encodes a Key object with Base64
	 * @param key The PublicKey or PrivateKey to encode
	 * @return The base64 encoded key string
	 */
	public static String encodeDSA(Key key)
	{
		byte[] array = key.getEncoded();
		return encodeBase64(array);
	}

	/**
	 * Encodes a byte array as Base64 string
	 * @param bytes The byte array to encode
	 * @return The base64 encoded string
	 */
	public static String encodeBase64(byte[] bytes)
	{
		Encoder encoder = Base64.getEncoder();
		byte[] out = encoder.encode(bytes);
		return new String(out);
	}
	
	public static void generateKeyPair(){
		
		
	}

}
