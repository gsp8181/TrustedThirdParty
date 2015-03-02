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
import java.net.URL;
import sun.net.www.http.HttpClient;
import com.sun.jmx.snmp.tasks.ThreadService;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;


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
			//countersign();
			break;
		case "gensig":
			genSig(args);
			break;
		case "getcompleted":
			//getcompleted();
			break;
		case "getcontracts":
			//getcontracts();
			break;
		case "sign":
			//sign();
			break;
		default:
			printHelp();
			return;
		}
		
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( OptionsFactory.allOptions(), args );
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp", OptionsFactory.returnOptions() );
	    	return;
	    }
	    
	    
	    
	    
	    

	}


	private static void genSig(String[] args) {
		// TODO Auto-generated method stub
			System.out.println("Hi, i am front end.");
			
			  // Generate a DSA signature 

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
	
	
	public static void test(String username){
		
		
	}
	
	public static void sendRequest() throws Exception{
		SendPostRequest post;
		try {
			post = new SendPostRequest();
			post.send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public static void  sendpostjson(){
		
		
//		URI uri = new URIBuilder().setScheme("http")
//                .setHost("jbosscontactsangularjs-110060653.rhcloud.com")
//                .setPath("/rest/bookings").build();
//        HttpPost req = new HttpPost(uri);
//        StringEntity params = new StringEntity("{\"customerId\":\""
//                + travelAgentTaxi.toString() + "\",\"taxiId\":\""
//                + travelSketch.getTaxiId().toString() + "\",\"bookingDate\":\""
//                + travelSketch.getBookingDate() + "\"}");
//        req.addHeader("Content-Type", "application/json");
//        req.setEntity(params);
//        CloseableHttpResponse response = httpClient.execute(req);
//        if (response.getStatusLine().getStatusCode() != 201) {
//            throw new Exception("Failed to create a flight booking");
//        }
//        String responseBody = EntityUtils.toString(response.getEntity());
//        JSONObject responseJson = new JSONObject(responseBody);
//        long rtn = responseJson.getLong("id");
//        HttpClientUtils.closeQuietly(response);
//        return rtn;
//		
//		
		
//		
//
//	    HttpClient httpClient = new DefaultHttpClient();
//
//	    try {
//	        HttpPost request = new HttpPost("http://yoururl");
//	        StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
//	        request.addHeader("content-type", "application/x-www-form-urlencoded");
//	        request.setEntity(params);
//	        HttpResponse response = httpClient.execute(request);
//
//	        // handle response here...
//	    }catch (Exception ex) {
//	        // handle exception here
//	    } finally {
//	        httpClient.getConnectionManager().shutdown();
//	    }
	}
	
	public static void saveToFile() {
		CreateXML creates = new CreateXML();
		creates.create(thepublic, thesign);
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
