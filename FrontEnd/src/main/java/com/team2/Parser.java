package com.team2;


import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
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
	
	
	static
	{
		
		 try{
			 ObjectInputStream inb =new ObjectInputStream(new FileInputStream("user"));
			   user =  (User)inb.readObject();			   	   
		 } 
		   catch (Exception e) {}
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
		
		switch(command)
		{
		case "countersign":
			return counterSign(args);
			
		case "gensig":
			return generateSig(args);
			
		case "getcompleted":
			return getCompleted(args);
			
		case "getcontracts":
			return getContract();
			
		case "sign":
			return sendContract(args);
						
		case "abort":			
			return abort(args);
			
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
			TimeStampedKey  t= CertificateTools.genTimestamp(key);
			Response record = get(hostName + "/contracts/2/"+user.getSig().getSignedData()+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
			return record.asString();			
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			
			e.printStackTrace();
			return null;
		}
	
		
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
			TimeStampedKey  t= CertificateTools.genTimestamp(key);
			Response doc = get(hostName + "/contracts/5/"+id+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
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
			TimeStampedKey  t= CertificateTools.genTimestamp(key);
			Response res = delete(hostName + "/contracts/abort/"+id+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
			return res.asString();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	
}
