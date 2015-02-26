package com.team2.jax.contract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;

public class ContractTempFileStore {

	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	
	public String save(String docName, byte[] doc) {
		//String fileName = TMP_DIR + docName;
		
		OutputStream out;
		try {
			File fileName = getFile(docName);
			
			out = new FileOutputStream(fileName,false);
		
		
		out.write(doc);
		
		out.close();
		
		return "http://localhost:8080/service/docs/" + docName;
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	public File getFile(String name) throws Exception
	{
		// URL returned "/C:/Program%20Files/Tomcat%206.0/webapps/myapp/WEB-INF/classes/"
		URL r = this.getClass().getResource("/");

		// path decoded "/C:/Program Files/Tomcat 6.0/webapps/myapp/WEB-INF/classes/"
		String decoded = URLDecoder.decode(r.getFile(), "UTF-8").replace("WEB-INF/classes/", "docs/");

		if (decoded.startsWith("/")) {
		    // path "C:/Program Files/Tomcat 6.0/webapps/myapp/WEB-INF/classes/"
		    decoded = decoded.replaceFirst("/", "");
		}
		File f = new File(decoded, name);
		
		return f;
	}

}
