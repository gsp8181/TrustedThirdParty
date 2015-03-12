package com.team2;


import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.json.JSONArray;








import org.json.JSONObject;

import com.team2.security.*;


public class Parser {
	private static User user;
	private static String docData= "SGVsbG8sIHdvcmxkIQ==";	
	private static final String hostName = "https://ttp.gsp8181.co.uk/rest";
	private static boolean hasArgs;
	
	static
	{
		
		 try{
			 ObjectInputStream inb =new ObjectInputStream(new FileInputStream("user"));
			   user =  (User)inb.readObject();	
			   if (user == null)
				   hasArgs = false;
			   hasArgs = true;
		 } 
		   catch (Exception e) {hasArgs = false;}
	}
	

	public void print(String[] args)
	{
		String result = parse(args);
		if(result==null){printHelp();}
		else {System.out.println(result);}
	}
	
	private String parse(String[] args) {

		// If there are no args, return;
		if(args.length < 1)
		{			
	    	return null;
		}
		String command = args[0];		
		
		switch (command) {
		case "countersign":
			if (noSigError())
				return counterSign(args);
			return null;

		case "gensig":
			return generateSig(args);

		case "getcompleted":
			if (noSigError())
				return getCompleted(args);
			return null;

		case "getcontracts":
			if (noSigError())
				return getContract();
			return null;

		case "sign":
			if (noSigError())
				return sendContract(args);
			return null;

		case "abort":
			if (noSigError())
				return abort(args);
			return null;

		default:
			return null;
		}
	    
	}

	
	private void printHelp() {
		System.out.println("usage: ttp <command>");
		System.out.println("gensig:	        Generates a certificate and a signature according to the given email address");
		System.out.println("countersign:	Countersigns a document");
		System.out.println("getcompleted:	Returns the receipt signature of a remote document");
		System.out.println("getcontracts:	Returns all contracts waiting to be signed");
		System.out.println("sign:	        Signs a document and submits it with the current");
	} 
	
	private boolean noSigError()
	{
		if(!hasArgs)
			System.err.println("There is no signature stored, add one before trying to use contract features");
		
		return hasArgs;
	}
	
	
	private String generateSig(String[] args){
		
		 CommandLineParser parser = new GnuParser();
		    try {
		      
		       CommandLine line = parser.parse( OptionsFactory.gensigOptions(), args );
		       String email = line.getOptionValue("e");
		       if(Validate.verify(email))
		       {
		    	   return initialize(email);
		       }
		       else 
		       {
		    	   System.out.println("The email address must be in the format of name@domain.com");
		    	   return null;
		       }	
		     
		    }
		    catch( ParseException exp ) {
		       
		    	HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp( "ttp generateSig", OptionsFactory.gensigOptions() );
		    	return null;
		    }
			
		
		
	}
	
	private String initialize(String email){
		User u = new User();
		try {
			Sig sig = CertificateTools.getTestData(email);						
			u.setSig(sig);			
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException e) {
    		e.printStackTrace();
    		return null;
			}	
		
		System.out.println("Signature generated for user: "+email);
		u.getSig().print();
		user = u;
		try {
			   ObjectOutputStream obj = new ObjectOutputStream (new FileOutputStream("user"));
			   obj.writeObject(u);
			   obj.close();
		} catch (IOException e) {			
			e.printStackTrace();
			return null;
		}
		JSONObject json = new JSONObject()
				.append("publicKey", u.getSig().getPublicKeyBase64())
				.append("email", email)
				.append("signedData", u.getSig().getSigBase64());
				
		 Response res = given().
			contentType(ContentType.JSON).
			body(json).
			post(hostName + "/certificates/");	
		 
		return res.asString();
		
		   
		   
	
	}
	
	private String sendContract(String[] args){
		 CommandLineParser parser = new GnuParser();
		    try {
		      
		       CommandLine line = parser.parse( OptionsFactory.signOptions(), args );
		       String destination = line.getOptionValue("d");
		       String fileName = line.getOptionValue("f");
		       if(Validate.verify(destination))		       
		       {
		    	   return sign(fileName, destination);
		       }
		       else 
		       {
		    	   System.out.println("The email address must be in the format of name@domain.com");
		    	   return null;
		       }	
		     
		    }
		    catch( ParseException exp ) {
		       
		    	HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp( "ttp sign", OptionsFactory.signOptions() );
		    	return null;
		    }
		
	}
	
	private String sign(String fileName,String destination){	
		try {
			JSONObject json = new JSONObject()
					.append("docData", docData)
					.append("docName", fileName)
					.append("email", user.getSig().getSignedData())
					.append("recipient", destination)
					.append("sig",
							CertificateTools.signData(docData, CertificateTools
									.decodeDSAPriv(user.getSig()
											.getPrivateKeyBase64())));
			
			Response res = given().
					contentType(ContentType.JSON).
					body(json).
					post(hostName + "/contracts/1/");
			return res.asString();
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String getContract(){
		
		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64());
			Response record = get(hostName + "/contracts/2/"+user.getSig().getSignedData(), timeStampArgs(key));
			return record.asString();			
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			
			e.printStackTrace();
			return null;
		}
	
		
	}

	private Map<String, String> timeStampArgs(PrivateKey key)
			throws InvalidKeyException, SignatureException,
			NoSuchAlgorithmException {
		TimeStampedKey  t= CertificateTools.genTimestamp(key);
		Map<String, String> args = new HashMap<String, String>();
		args.put("ts", String.valueOf(t.getTime()));
		args.put("signedStamp", t.getSignedKey());
		return args;
	}
	
	private String counterSign(String[] args){
		 CommandLineParser parser = new GnuParser();
		    try {
		      
		       CommandLine line = parser.parse( OptionsFactory.countersignOptions(), args );
		       String id = line.getOptionValue("i");
		       return signContract(id);
		     
		    }
		    catch( ParseException exp ) {
		       
		    	HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp( "ttp generateSig", OptionsFactory.gensigOptions() );
		    	return null;
		    }
	}
	
	private String signContract(String id){
		JSONArray list = new JSONArray(getContract());
		String eoc=null;
		for(int i=0;i<list.length();i++)
		{
			
			if(list.getJSONObject(i).get("id").equals(id)){eoc=list.getJSONObject(i).getString("sigSender");}
		}
		if(eoc==null){return null;}
		
		try {
			JSONObject json1 = new JSONObject().append("sig",CertificateTools.signData(eoc, CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64())));
			Response sign = given().
					contentType(ContentType.JSON).
					body(json1).
					post(hostName + "/contracts/3/"+id);
			return sign.asString();
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
				return null;
		}
		
	}
	
	
	private String getCompleted(String[] args){
		
			 CommandLineParser parser = new GnuParser();
			    try {
			      
			       CommandLine line = parser.parse( OptionsFactory.getcompletedOptions(), args );
			       String id = line.getOptionValue("i");
			       return completedContract(id);
			     
			    }
			    catch( ParseException exp ) {
			       
			    	HelpFormatter formatter = new HelpFormatter();
			    	formatter.printHelp( "ttp sign", OptionsFactory.signOptions() );
			    	return null;
			    }
			
	}
	
	private String completedContract(String id){
		 
		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64());
			Response doc = get(hostName + "/contracts/5/"+id, timeStampArgs(key));
		    return doc.asString();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			return null;
		}
			
	}
	
	private String abort(String[] args)
	{
		CommandLineParser parser = new GnuParser();
	    try {
	      
	       CommandLine line = parser.parse( OptionsFactory.abort(), args );
	       String id = line.getOptionValue("i");
	       return deleteRecord(id);
	     
	    }
	    catch( ParseException exp ) {
	       
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "ttp sign", OptionsFactory.signOptions() );
	    	return null;
	    }
	}
	
	private String deleteRecord(String id)
	{System.out.println(id);
		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64());
			Response res = delete(hostName + "/contracts/abort/"+id, timeStampArgs(key));
			return res.asString();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	
}
