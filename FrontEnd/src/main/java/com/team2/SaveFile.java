package com.team2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.team2.security.TestData;

public class SaveFile {
	public static void save(String pubKey, String privKey, String email,
			String signedData) throws IOException {

		TestData obj = new TestData(pubKey, email, signedData, privKey);

		save(obj);
	}

	public static void save(TestData data) throws IOException {
		String workingDir = System.getProperty("user.dir");
		FileOutputStream fileOut = new FileOutputStream(workingDir
				+ "\\settings.xml");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(data);
		out.close();
		fileOut.close();
	}

	public static TestData read() throws IOException, ClassNotFoundException {
		TestData obj = null;
		String workingDir = System.getProperty("user.dir");
		FileInputStream fileIn;
		fileIn = new FileInputStream(workingDir + "\\settings.xml");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		obj = (TestData) in.readObject();
		in.close();
		fileIn.close();
		return obj;
	}
}
