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

import com.team2.security.*;


public class Main extends CertificateTools {
	
	
	private static String thePublic = null;
	private static String thePrivate = null;
	private static String theEmail = null;
	private static String theSign = null;
	private static String theTime = null;
	private static String thePublicResponse = null;
	private static String theCode = null;
	private static boolean theStatus  = false;
	private static String id = null;
	private static String documentText64 = null;
	private static String theDocumentName = null;
	private static String theRecipient = null;
	private static String theUsername = null;
	private static String theSigSender = null;
	

	public static void main(String[] args) {
		
		
		// If there are no args, return;
		if(args.length < 1)
		{
			printHelp();
	    	return;
		}
		String command = args[0];
		/*if(args.length == 1)
			args = null;
		else
			args = ArrayUtils.removeElement(args,0);
			*/
		
		
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
	       String id = line.getOptionValue("i");
	       doCounterSign(id);
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp countersign", OptionsFactory.countersignOptions() );
	    	return;
	    }
		
	}
	
	private static void doCounterSign(String id) {
		// TODO Auto-generated method stub
		
	}

	private static void getcompleted(String[] args){
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.getcompletedOptions(), args );
	       String id = line.getOptionValue("i");
	       doGetCompleted(id);
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp getcompleted", OptionsFactory.getcompletedOptions() );
	    	return;
	    }
		
	}
	
	
	private static void doGetCompleted(String id) {
		// TODO Auto-generated method stub
		
	}

	private static void getcontracts(String[] args){

		
	}
	
	private static void sign(String[] args){
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.signOptions(), args );
	       String destination = line.getOptionValue("d");
	       String filename = line.getOptionValue("f");
	       doSign(destination,filename);
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp sign", OptionsFactory.signOptions() );
	    	return;
	    }
		
	}
	
	


	private static void doSign(String destination, String filename) {
		// TODO Auto-generated method stub
		try
		{
		// hello world (base64) = aGVsbG8gd29ybGQ=
		// temp - need to upgrade later
		String docText = "aGVsbG8gd29ybGQ=";
		System.out.println("Receipient : " + destination);
		System.out.println("Document Name : " + filename);
		System.out.println("Document text : " + docText);
		System.out.println("Sign : " + theSign);
		
		System.out.println("Email : " + theEmail);
		//save the key
		//saveToFile(); only save 2 parameter
		//saveXML();
		
		JSONObject send = new JSONObject().put("recipient",destination)
				.put("docName", filename)
				.put("sig", theSign)
				.put("docData", docText)
				.put("email",theEmail);
		URI uri = buildUri("ttp.gsp8181.co.uk","/contract/1",80,false,null);
		JSONObject response = sendpostjson(uri, send);
//		System.out.println(response.getString("code"));
//		System.out.println("Public Key : " + response.getString("publicKey"));
		storeRespondsGenSig(response);
		
		}	         catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
            e.printStackTrace();
        }
		
		
		
		
	}

	private static void genSig(String[] args) {

		
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	       CommandLine line = parser.parse( OptionsFactory.gensigOptions(), args );
	       String email = line.getOptionValue("e");
	       if(isValidEmail(email))
	       doGenSig(email);
	       else{
	    	   System.out.println("Please enter valid email address.");
	    	   return;
	       }
	    	
	       
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp gensig", OptionsFactory.gensigOptions() );
	    	return;
	    }
		
	}
	
	/*
	 * send get request to get the certificate by email receipient
	 * 
	 * */
	private static void getCertificateByEmail() {
		try
		{
		// TODO Auto-generated method stub
		System.out.println("Email Receipient : " + theEmail);
		URI uri = buildUri("ttp.gsp8181.co.uk","/rest/certificates/verify",80,false,"email", theEmail);
		JSONObject response = sendgetjson(uri);
//		System.out.println("Verify status : " + response.toString());
		displayCertificate(response);
		
		}	         catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
            e.printStackTrace();
        }
	}
	
	/*
	 * send get request to verify the certificate
	 * 
	 * */
	private static void doVerifyCertificate() {
		try
		{
		// TODO Auto-generated method stub
		System.out.println("Email Receipient : " + theEmail);
		System.out.println("Code" + theCode);
		URI uri = buildUri("ttp.gsp8181.co.uk","/rest/certificates/verify",80,false,"email", theEmail, "code", theCode);
		JSONObject response = sendgetjson(uri);
//		System.out.println(response.getString("code"));
		System.out.println("Verify status : " + response.toString());
//		storeRespondsGenSig(response);
		
		}	         catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
            e.printStackTrace();
        }
	}

	private static void doGenSig(String email) {
		try
		{
		// TODO Auto-generated method stub
		System.out.println("Email Receipient : " + email);
		theEmail = email;
		//generate the key here
		generatingKeyTest();
		System.out.println("Public key : " + thePublic);
		System.out.println("Signed private key : " + theSign);
		System.out.println("Private key : " + thePrivate);
		//save the key
		//saveToFile(); only save 2 parameter
		saveXML();
		
		JSONObject send = new JSONObject().put("publicKey",thePublic).put("signedData", theSign).put("email",theEmail);
		URI uri = buildUri("ttp.gsp8181.co.uk","/rest/certificates/",80,false,null);
		JSONObject response = sendpostjson(uri, send);
		System.out.println(response.getString("code"));
//		System.out.println("Public Key : " + response.getString("publicKey"));
		storeRespondsGenSig(response);
		
		}	         catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
            e.printStackTrace();
        }
	}
	
	
	public static void storeRespondsGenSig(JSONObject response){
		theTime = response.getString("time");
		thePublicResponse = response.getString("publicKey");
		theCode = response.getString("code");
		theStatus = response.getBoolean("status");
	}

	public static void displayRespondSign(JSONObject response){
		System.out.println("ID : " + response.getString("id"));
		System.out.println("User name : " + response.getString("username"));
		System.out.println("Recipient :" +  response.getString("recipient"));
		System.out.println("Document Name :" + response.getString("docName"));
		System.out.println("Signature Sender :" + response.getString("sigSender"));
	}

	public static void displayCertificate(JSONObject response){
		System.out.println("Public Key : " + response.getString("publicKey"));
		System.out.println("Time : " + response.getString("time"));
		System.out.println("Code :" +  response.getString("code"));
		System.out.println("Status :" + response.getString("status"));
		System.out.println("Email :" + response.getString("email"));
	}


	private static void printHelp() {
		System.out.println("usage: ttp <command>");
		System.out.println("countersign:	Countersigns a document");
		System.out.println("gensig:			Generates a signature for use in the program");
		System.out.println("getcompleted:	Returns the receipt signature of a remote document");
		System.out.println("getcontracts:	Returns all contracts waiting to be signed");
		System.out.println("sign:			Signs a document and submits it with the current");
	}
	
	public static boolean isValidEmail(String email){
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean valid = email.matches(EMAIL_REGEX);
		if (!valid) {
			System.err.println("Caught exception " + " Email is not valid : " + email);
			return false;
		} else {
			return true;
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
		if(query != null && !query.isEmpty())
			uri.setQuery(query);
		return uri.build();
	}
	
	public static URI buildUri(String hostname, String path, int port, boolean secure, String param1, String arg1) throws URISyntaxException
	{
		URIBuilder uri = new URIBuilder();
		if(secure)
			uri.setScheme("https");
		else
			uri.setScheme("http");
		
		uri.setHost(hostname);
		uri.setPath(path);
		uri.setPort(port);
		if(param1 != null && !arg1.isEmpty())
			uri.setParameter(param1, arg1);
		return uri.build();
	}
	
	public static URI buildUri(String hostname, String path, int port, boolean secure, String param1, String arg1, String param2, String arg2) throws URISyntaxException
	{
		URIBuilder uri = new URIBuilder();
		if(secure)
			uri.setScheme("https");
		else
			uri.setScheme("http");
		
		uri.setHost(hostname);
		uri.setPath(path);
		uri.setPort(port);
		if(param1 != null && !arg1.isEmpty()){
			uri.setParameter(param1, arg1);
			if(param2 != null && !arg2.isEmpty())
				uri.setParameter(param2, arg2);
		}
		return uri.build();
	}
	
	public static URI buildUri(String hostname, String path, int port, boolean secure, String param1, String arg1, String param2, String arg2, String param3, String arg3) throws URISyntaxException
	{
		URIBuilder uri = new URIBuilder();
		if(secure)
			uri.setScheme("https");
		else
			uri.setScheme("http");
		
		uri.setHost(hostname);
		uri.setPath(path);
		uri.setPort(port);
		if(param1 != null && !arg1.isEmpty()){
			uri.setParameter(param1, arg1);
			if(param2 != null && !arg2.isEmpty()){
				uri.setParameter(param2, arg2);
				if(param3 != null && !arg3.isEmpty())
					uri.setParameter(param3, arg3);
			}
		}
		return uri.build();
	}
	
	public static JSONObject  sendgetjson(URI endpoint) throws Exception{
		CloseableHttpClient  httpClient = HttpClients.createDefault();
		
			HttpGet req = new HttpGet(endpoint);
			CloseableHttpResponse response = httpClient.execute(req);
			String responseBody = EntityUtils.toString(response.getEntity());
			if(!response.getStatusLine().toString().startsWith("HTTP/1.1 2"))
				throw new Exception("Failed to GET : error " + response.getStatusLine().toString());
			return new JSONObject(responseBody); //responseJson.getLong("id"); example
		}
	
	public static JSONObject  sendpostjson(URI endpoint, JSONObject message) throws Exception{
		CloseableHttpClient  httpClient = HttpClients.createDefault();
		
		StringEntity params = new StringEntity(message.toString());
			HttpPost req = new HttpPost(endpoint);
			
			req.addHeader("Content-Type", "application/json");
			req.setEntity(params);
			CloseableHttpResponse response = httpClient.execute(req);
			String responseBody = EntityUtils.toString(response.getEntity());
			if(!response.getStatusLine().toString().startsWith("HTTP/1.1 2"))
				throw new Exception("Failed to POST : error " + response.getStatusLine().toString() + ", " + responseBody);
			return new JSONObject(responseBody); //responseJson.getLong("id"); example
		}

	
	public static void saveXML() {
		CreateXML creates = new CreateXML();
		creates.create(thePublic, thePrivate, theEmail, theSign);
	}
	
	public static void saveToFile() {
		CreateXML creates = new CreateXML();
		creates.create(thePublic, theSign);
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
		dsa.update(theEmail.getBytes());
		String sig = encodeBase64(dsa.sign());
		//set the value
		thePublic = encodedKey;
		theSign = sig;
		thePrivate = encodedKeyPrivate;
		
		
	}
	
	
	public static void generatingKeyTest() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException{
		
		/*KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		
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
		thePrivate = encodedKeyPrivate;*/
		
		
		
		TestData test = getTestData(theEmail);
		
		thePublic = test.publicKeyBase64;
		thePrivate = test.privateKeyBase64;
		theSign = test.sigBase64;
		
		
	}
	
}
