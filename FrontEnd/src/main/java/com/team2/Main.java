package com.team2;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import sun.net.www.http.HttpClient;

import com.sun.jmx.snmp.tasks.ThreadService;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class Main {
	
	
	private static String thepublic = null;
	private static String thePrivate = null;
	private static String username = null;
	private static String thesign = null;
	

	public static void main(String[] args) {
		
		
		// If there are no args, return;
		if(args.length < 1)
		{
			printHelp();
	    	return;
		}
		String command = args[0];
		
		switch(command)
		{
		case "countersign":
			countersign(args);
			break;
		case "gensig":
			genSig(args);
			break;
		case "getcompleted":
			getcompleted(args);
			break;
		case "getcontracts":
			getcontracts(args);
			break;
		case "sign":
			sign(args);
			break;
		default:
			printHelp();
			return;
		}
	    
	}
	
	private static void countersign(String[] args){
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.countersignOptions(), args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp countersign", OptionsFactory.countersignOptions() );
	    	return;
	    }
		
	}
	
	private static void getcompleted(String[] args){
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.getcompletedOptions(), args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp getcompleted", OptionsFactory.getcompletedOptions() );
	    	return;
	    }
		
	}
	
	
	private static void getcontracts(String[] args){
	   /* // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.returnOptions(), args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp getcontracts", OptionsFactory.returnOptions() );
	    	return;
	    }*/
		
	}
	
	private static void sign(String[] args){
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.signOptions(), args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp sign", OptionsFactory.signOptions() );
	    	return;
	    }
		
	}
	
	


	private static void genSig(String[] args) {

		
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.gensigOptions(), args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp gensig", OptionsFactory.gensigOptions() );
	    	return;
	    }
		
	        if (args.length != 1) {
	            System.out.println("Usage: GenSig username");
	        }
	        else try {
	        	
	        	if (args[0] != null) {
					username = args[0];
					System.out.println("User name : " + username);
					//generate the key here
					generatingKeyTest();
					System.out.println("Public key : " + thepublic);
					System.out.println("Signed private key : " + thesign);
					System.out.println("Private key : " + thePrivate);
					//save the key
					saveToFile();
					//send post request
				}
//	        	read user input for username
	        	test(args[0]);
//	        	generatingKeyTest();
	        // the rest of the code goes here

	        } catch (Exception e) {
	            System.err.println("Caught exception " + e.toString());
	        }
	}


	private static void printHelp() {
		System.out.println("usage: ttp <command>");
		System.out.println("countersign:	Countersigns a document");
		System.out.println("gensig:			Generates a signature for use in the program");
		System.out.println("getcompleted:	Returns the receipt signature of a remote document");
		System.out.println("getcontracts:	Returns all contracts waiting to be signed");
		System.out.println("sign:			Signs a document and submits it with the current");
	}
	
	public static void test(String email){
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean valid = email.matches(EMAIL_REGEX);
		if (!valid) {
			System.err.println("Caught exception " + " Email is not valid : " + email);
		} else {
			
		}
	}
	
	/**
	 * Builds a URI object from the variables provided
	 * @param hostname The hostname for example www.google.co.uk
	 * @param path The path of the request for example /service/rest/contracts/0
	 * @param port The port of the host
	 * @param secure True to use https and false to use http
	 * @param query If using a query param for example login=true&things=this then set otherwise leave as null
	 * @return A URI object
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("deprecation")
	public static URI buildUri(String hostname, String path, int port, boolean secure, String query) throws URISyntaxException
	{
		URIBuilder uri = new URIBuilder();
		if(secure)
			uri.setScheme("https");
		else
			uri.setScheme("http");
		
		uri.setHost(hostname);
		uri.setPath(path);
		uri.setPort(port);
		if(query != null || !query.isEmpty())
			uri.setQuery(query);
		return uri.build();
	}
	
	public static JSONObject  sendgetjson(URI endpoint) throws Exception{
		CloseableHttpClient  httpClient = HttpClients.createDefault();
		
			HttpGet req = new HttpGet(endpoint);
			CloseableHttpResponse response = httpClient.execute(req);
			String responseBody = EntityUtils.toString(response.getEntity());
			if(!response.getStatusLine().toString().startsWith("20"))
				throw new Exception("Failed to GET : error " + response.getStatusLine().toString());
			return new JSONObject(responseBody); //responseJson.getLong("id"); example
		}
	
	public static JSONObject  sendpostjson(URI endpoint, String message) throws Exception{
		CloseableHttpClient  httpClient = HttpClients.createDefault();
		
		StringEntity params = new StringEntity(message);
			HttpPost req = new HttpPost(endpoint);
			
			req.addHeader("Content-Type", "application/json");
			req.setEntity(params);
			CloseableHttpResponse response = httpClient.execute(req);
			String responseBody = EntityUtils.toString(response.getEntity());
			if(!response.getStatusLine().toString().startsWith("20"))
				throw new Exception("Failed to POST : error " + response.getStatusLine().toString());
			return new JSONObject(responseBody); //responseJson.getLong("id"); example
		}

	
	public static void saveToFile() {
		CreateXML creates = new CreateXML();
		creates.create(thepublic, thesign);
	}
	
	
	
public static void generatingKeyV2() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException{
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		
		keyGen.initialize(1024, random);
		//generate key
		KeyPair pair = keyGen.generateKeyPair();
		//private key object
		PrivateKey priv = pair.getPrivate();
		//public key object
		PublicKey pub = pair.getPublic();
		//encoded public key
		String encodedKey = encodeDSA(pub);
		//encoded private key
		String encodedKeyPrivate = encodeDSA(priv);
		
		Signature dsa = Signature.getInstance("SHA1withDSA");
		
		dsa.initSign(priv);
		dsa.update(username.getBytes());
		String sig = encodeBase64(dsa.sign());
		//set the value
		thepublic = encodedKey;
		thesign = sig;
		thePrivate = encodedKeyPrivate;
		
		
	}
	
	
	public static void generatingKeyTest() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException{
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		
		keyGen.initialize(1024, random);
		//generate key
		KeyPair pair = keyGen.generateKeyPair();
		//private key object
		PrivateKey priv = pair.getPrivate();
		//public key object
		PublicKey pub = pair.getPublic();
		//encoded public key
		String encodedKey = encodeDSA(pub);
		//encoded private key
		String encodedKeyPrivate = encodeDSA(priv);
		
		Signature dsa = Signature.getInstance("SHA1withDSA");
		
		dsa.initSign(priv);
		dsa.update(username.getBytes());
		String sig = encodeBase64(dsa.sign());
		//set the value
		thepublic = encodedKey;
		thesign = sig;
		thePrivate = encodedKeyPrivate;
		
		
	}
	
	/**
	 * Returns an object containing an example private/public key pair and signed data. 
	 * Should only be used for testing
	 * @param dataToSign The data to sign, usually the username
	 * @return The TestData object with the signed data and the keypair
	 */
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
	
}
