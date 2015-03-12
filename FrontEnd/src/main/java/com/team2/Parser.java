package com.team2;


import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;












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
	private static final String hostName = "https://ttp.gsp8181.co.uk/rest";
	private static boolean hasArgs;
	
	static
	{
		
		 try{
			 ObjectInputStream inb =new ObjectInputStream(new FileInputStream(System.getProperty("user.home") + "/.ttp/user.ttpsettings"));
			   user =  (User)inb.readObject();	
			   inb.close();
			   if (user == null)
				   hasArgs = false;
			   hasArgs = true;
		 } 
		   catch (Exception e) {hasArgs = false;}
	}
	

	public void print(String[] args)
	{
		String result = parse(args);
		if(result!=null){System.out.println(result);}
	}
	
	private String parse(String[] args) {

		// If there are no args, return;
		if(args.length < 1)
		{
			printHelp();
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
			printHelp();
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
			File dir = new File(System.getProperty("user.home") + "/.ttp/");
			dir.mkdir();
			   ObjectOutputStream obj = new ObjectOutputStream (new FileOutputStream(System.getProperty("user.home") + "/.ttp/user.ttpsettings")); //TODO end?: TODO: overwrite protection?
			   obj.writeObject(u);
			   obj.close();
		} catch (IOException e) {			
			e.printStackTrace();
			return null;
		}
		JSONObject json = new JSONObject()
				.put("publicKey", u.getSig().getPublicKeyBase64())
				.put("email", email)
				.put("signedData", u.getSig().getSigBase64());
				
		URI endpoint = HttpMethods.buildUri("/certificates/",null);
		System.out.println("Submitting to server");
		try
		{
		 JSONObject res = HttpMethods.sendpostjson(endpoint, json);
		 return res.getString("code");
		} catch (Exception e)
		{
			System.err.println("Failed to send");
			return e.getMessage();
		}
		 
		
		
		   
		   
	
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
		
		File f = new File(fileName);
		Path p = f.toPath(); //todo: fqn or local name
		if(!Files.isReadable(p))
			return "Could not access \"" + p.getFileName() + "\", make sure it exists and can be read";
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(p);
		} catch (IOException e1) {
			return "READ ERROR, " + e1.getMessage();
			
		}
		String data = CertificateTools.encodeBase64(encoded);
		
		JSONObject json;
		try {
			json = new JSONObject()
					.put("docData", data)
					.put("docName", p.getFileName())
					.put("email", user.getSig().getSignedData())
					.put("recipient", destination)
					.put("sig",
							CertificateTools.signData(data, CertificateTools
									.decodeDSAPriv(user.getSig()
											.getPrivateKeyBase64())));
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
			

			URI endpoint = HttpMethods.buildUri("/contracts/1/",null);
			try
			{
			 JSONObject res = HttpMethods.sendpostjson(endpoint, json);
			 String contractId = res.getString("id");
			 return "Contract accepted, the recipient has been emailed. The contract ID for reference is: " + contractId; //TODO !
			} catch (Exception e)
			{
				return e.getMessage();
			}

	}
	
	private JSONArray getContractArray() throws Exception{ //TODO: should be JSON object?
		
		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64());
			URI endpoint = HttpMethods.buildUri("/contracts/2/" + user.getSig().getSignedData(),timeStampArgs(key)); //TODO: nope
			try
			{
			 JSONArray res = HttpMethods.sendgetjsonArray(endpoint);
			 return res;
			} catch (Exception e)
			{
				if(e.getMessage().contains("404"))
					return null;
				throw (e);
			}
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	private String getContract()
	{
		JSONArray results;
		try {
			results = getContractArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String resultString = "";
		if(results == null)
			return "No contracts found";
		for(int i=0;i<results.length();i++)
		{
			
			JSONObject obj = results.getJSONObject(i);
			String ri = "Contract " + (i + 1) + "\n";
			ri += "ID: " + obj.getString("id") + "\n";
			ri += "From: " + obj.getString("sender") + "\n";
			ri += "Filename: " + obj.getString("docName") + "\n";
			ri += "Evidence of Origin: " + obj.getString("sigSender") + "\n";
					
			resultString += ri;
		}
		return resultString;
		
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
		    	formatter.printHelp( "ttp generateSig", OptionsFactory.gensigOptions() ); //TODO: handle 404
		    	return null;
		    }
	}
	
	private String signContract(String id){
		JSONArray list;
		try {
			list = getContractArray();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		String eoc=null;
		for(int i=0;i<list.length();i++)
		{
			
			if(list.getJSONObject(i).get("id").equals(id)){eoc=list.getJSONObject(i).getString("sigSender");}
		}
		if(eoc==null){return null;}
		
		try {
			JSONObject json1 = new JSONObject().put("sig",CertificateTools.signData(eoc, CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64())));

			URI endpoint = HttpMethods.buildUri("/contracts/3/" + id,null);
			try
			{
			 JSONObject res = HttpMethods.sendpostjson(endpoint, json1);
			 String docRef = res.getString("docRef");
			 return "The countersign was accepted and your document is now available for download at " + docRef;
			} catch (Exception e)
			{
				return e.getMessage();
			}
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

			
			URI endpoint = HttpMethods.buildUri("/contracts/5/" + id,timeStampArgs(key));
			try
			{
			 JSONObject res = HttpMethods.sendgetjson(endpoint);
			 String sig = res.getString("sig");
			 return "Contract ID: " + id + " has signature " + sig;
			} catch (Exception e)
			{
				return e.getMessage();
			}
			
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
	    	formatter.printHelp( "ttp sign", OptionsFactory.abort() );
	    	return null;
	    }
	}
	
	private String deleteRecord(String id)
	{
		
		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig().getPrivateKeyBase64());
			
			URI endpoint = HttpMethods.buildUri("/contracts/abort/" + id,timeStampArgs(key)); //TODO: nope
			try
			{
			 HttpMethods.senddeletejson(endpoint);
			 return "Contract aborted successfully";
			} catch (Exception e)
			{
				return e.getMessage();
			}
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			return null;
		}
	}	
	

	
	
}
