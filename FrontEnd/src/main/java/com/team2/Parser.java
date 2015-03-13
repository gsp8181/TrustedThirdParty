package com.team2;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.team2.security.CertificateTools;
import com.team2.security.Sig;
import com.team2.security.TimeStampedKey;

public class Parser {
	private static User user;
	private static final String hostName = "https://ttp.gsp8181.co.uk/rest";
	private static boolean hasArgs;

	static {

		try {
			ObjectInputStream inb = new ObjectInputStream(new FileInputStream(
					System.getProperty("user.home") + "/.ttp/user.ttpsettings"));
			user = (User) inb.readObject();
			inb.close();
			if (user == null)
				hasArgs = false;
			hasArgs = true;
		} catch (Exception e) {
			hasArgs = false;
		}
	}

	public void print(String[] args) {

		// If there are no args, return;
		if (args.length < 1) {
			printHelp();
			return;
		}
		String command = args[0];

		switch (command) {
		case "countersign":
			if (noSigError())
				counterSign(args);
			return;

		case "gensig":
			generateSig(args);
			return;

		case "getcompleted":
			if (noSigError())
				getCompleted(args);
			return;

		case "getcontracts":
			if (noSigError())
				getContract();
			return;

		case "sign":
			if (noSigError())
				sendContract(args);
			return;

		case "abort":
			if (noSigError())
				abort(args);
			return;

		default:
			printHelp();
			return;
		}

	}

	private void printHelp() {
		System.out.println("usage: ttp <command>");
		System.out
				.println("gensig:	        Generates a certificate and a signature\n\t\t according to the given email address");
		System.out.println("countersign:	Countersigns a document");
		System.out
				.println("getcompleted:	Returns the receipt signature of a remote document");
		System.out
				.println("getcontracts:	Returns all contracts waiting to be signed");
		System.out
				.println("sign:	        Signs a document and submits it with the current");
		System.out
				.println("abort:	Aborts a contract exchange in progress");
	}

	private boolean noSigError() {
		if (!hasArgs) {
			System.err
					.println("There is no signature stored, add one before trying to use contract features");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ttp generateSig",
					OptionsFactory.gensigOptions());
		}

		return hasArgs;
	}

	private void generateSig(String[] args) {

		CommandLineParser parser = new GnuParser();
		try {

			CommandLine line = parser.parse(OptionsFactory.gensigOptions(),
					args);
			String email = line.getOptionValue("e");
			if (Validate.verify(email)) {
				initialize(email);
			} else {
				System.out
						.println("The email address must be in the format of name@domain.com");
			}

		} catch (ParseException exp) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ttp generateSig",
					OptionsFactory.gensigOptions());
		}

	}

	private void initialize(String email) {
		User u = new User();
		try {
			Sig sig = CertificateTools.getTestData(email);
			u.setSig(sig);
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Signature generated for user: " + email);
		u.getSig().print();
		user = u;
		try {
			File dir = new File(System.getProperty("user.home") + "/.ttp/");
			dir.mkdir();
			ObjectOutputStream obj = new ObjectOutputStream(
					new FileOutputStream(System.getProperty("user.home")
							+ "/.ttp/user.ttpsettings"));
			obj.writeObject(u);
			obj.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		JSONObject json = new JSONObject()
				.put("publicKey", u.getSig().getPublicKeyBase64())
				.put("email", email)
				.put("signedData", u.getSig().getSigBase64());

		URI endpoint = HttpMethods.buildUri("/certificates/", null);
		System.out.println("Submitting to server");
		try {
			JSONObject res = HttpMethods.sendpostjson(endpoint, json);
			System.out.println(res.getString("code"));
		} catch (Exception e) {
			System.err.println("Failed to send");
		}
	}

	private void sendContract(String[] args) {
		CommandLineParser parser = new GnuParser();
		try {

			CommandLine line = parser.parse(OptionsFactory.signOptions(), args);
			String destination = line.getOptionValue("d");
			String fileName = line.getOptionValue("f");
			if (Validate.verify(destination)) {
				sign(fileName, destination);
			} else {
				System.out
						.println("The email address must be in the format of name@domain.com");
			}

		} catch (ParseException exp) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ttp sign", OptionsFactory.signOptions());
		}

	}

	private void sign(String fileName, String destination) {

		File f = new File(fileName);
		Path p = f.toPath(); // todo: fqn or local name
		if (!Files.isReadable(p)) {
			System.err.println("Could not access \"" + p.getFileName()
					+ "\", make sure it exists and can be read");
			return;
		}
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(p);
		} catch (IOException e1) {
			System.err.println("READ ERROR, " + e1.getMessage());
			return;

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
			return;
		}

		URI endpoint = HttpMethods.buildUri("/contracts/1/", null);
		try {
			JSONObject res = HttpMethods.sendpostjson(endpoint, json);
			String contractId = res.getString("id");
			System.out
					.println("Contract accepted, the recipient has been emailed. The contract ID for reference is: "
							+ contractId);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	private JSONArray getContractArray() throws Exception { 

		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig()
					.getPrivateKeyBase64());
			URI endpoint = HttpMethods.buildUri("/contracts/2/"
					+ user.getSig().getSignedData(), timeStampArgs(key)); 
			try {
				JSONArray res = HttpMethods.sendgetjsonArray(endpoint);
				return res;
			} catch (Exception e) {
				if (e.getMessage().contains("404"))
					return null;
				throw (e);
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException
				| InvalidKeyException | SignatureException e) {

			e.printStackTrace();
			return null;
		}

	}

	private void getContract() {
		JSONArray results;
		try {
			results = getContractArray();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		String resultString = "";
		if (results == null) {
			System.err.println("No contracts found");
			return;
		}
		for (int i = 0; i < results.length(); i++) {

			JSONObject obj = results.getJSONObject(i);
			String ri = "Contract " + (i + 1) + "\n";
			ri += "ID: " + obj.getString("id") + "\n";
			ri += "From: " + obj.getString("sender") + "\n";
			ri += "Filename: " + obj.getString("docName") + "\n";
			ri += "Evidence of Origin: " + obj.getString("sigSender") + "\n";

			System.out.println(ri);
		}

	}

	private Map<String, String> timeStampArgs(PrivateKey key)
			throws InvalidKeyException, SignatureException,
			NoSuchAlgorithmException {
		TimeStampedKey t = CertificateTools.genTimestamp(key);
		Map<String, String> args = new HashMap<String, String>();
		args.put("ts", String.valueOf(t.getTime()));
		args.put("signedStamp", t.getSignedKey());
		return args;
	}

	private void counterSign(String[] args) {
		CommandLineParser parser = new GnuParser();
		try {

			CommandLine line = parser.parse(
					OptionsFactory.countersignOptions(), args);
			String id = line.getOptionValue("i");
			signContract(id);

		} catch (ParseException exp) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ttp generateSig",
					OptionsFactory.gensigOptions()); // TODO: handle 404
		}
	}

	private void signContract(String id) {
		JSONArray list;
		try {
			list = getContractArray();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		String eoc = null;
		for (int i = 0; i < list.length(); i++) {

			if (list.getJSONObject(i).get("id").equals(id)) {
				eoc = list.getJSONObject(i).getString("sigSender");
			}
		}
		if (eoc == null) {
			System.err.println("Could not find contract with that ID");
			return;
		}

		try {
			JSONObject json1 = new JSONObject().put("sig", CertificateTools
					.signData(eoc, CertificateTools.decodeDSAPriv(user.getSig()
							.getPrivateKeyBase64())));

			URI endpoint = HttpMethods.buildUri("/contracts/3/" + id, null);
			try {
				JSONObject res = HttpMethods.sendpostjson(endpoint, json1);
				String docRef = res.getString("docRef");
				System.out
						.println("The countersign was accepted and your document is now available for download at "
								+ docRef);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		} catch (InvalidKeyException | SignatureException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}

	}

	private void getCompleted(String[] args) {

		CommandLineParser parser = new GnuParser();
		try {

			CommandLine line = parser.parse(
					OptionsFactory.getcompletedOptions(), args);
			String id = line.getOptionValue("i");
			completedContract(id);

		} catch (ParseException exp) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ttp sign", OptionsFactory.signOptions());
		}

	}

	private void completedContract(String id) {

		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig()
					.getPrivateKeyBase64());

			URI endpoint = HttpMethods.buildUri("/contracts/5/" + id,
					timeStampArgs(key));
			try {
				JSONObject res = HttpMethods.sendgetjson(endpoint);
				String sig = res.getString("sig");
				System.out.println("Contract ID: " + id + " has signature "
						+ sig);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException
				| InvalidKeyException | SignatureException e) {
			e.printStackTrace();
		}

	}

	private void abort(String[] args) {
		CommandLineParser parser = new GnuParser();
		try {

			CommandLine line = parser.parse(OptionsFactory.abort(), args);
			String id = line.getOptionValue("i");
			deleteRecord(id);

		} catch (ParseException exp) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ttp sign", OptionsFactory.abort());
		}
	}

	private void deleteRecord(String id) {

		try {
			PrivateKey key = CertificateTools.decodeDSAPriv(user.getSig()
					.getPrivateKeyBase64());

			URI endpoint = HttpMethods.buildUri("/contracts/abort/" + id,
					timeStampArgs(key));
			try {
				HttpMethods.senddeletejson(endpoint);
				System.out.println("Contract aborted successfully");
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException
				| InvalidKeyException | SignatureException e) {
			e.printStackTrace();
		}
	}

}
