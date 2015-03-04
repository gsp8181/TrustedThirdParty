package com.team2.jax.contract;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.team2.jax.contract.ContractFileStoreS3;

public class S3StorageTest {
	
	private static ContractFileStoreS3 contract;
	private static AmazonS3 s3 = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		contract = new ContractFileStoreS3();
		s3 = ContractFileStoreS3.connCreation();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Asserting returning value.
	 * Checking whether object is there.
	 * Asserting file content. (Downloading file and checking the content). 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws IOException
	 */
	public void testSaveFileSuccessful() throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		byte[] doc = "This is contract".getBytes();
		String rep = null;
		rep = contract.saveFile("fileName.txt", doc);
		
		// Assert returning value.
		assertEquals("fileName.txt", rep);

		S3Object obj = s3.getObject("contractbucket", "fileName.txt");
		
		// Assert object is exist.
		assertNotNull(obj);
		assertEquals(obj.toString(), "S3Object [key=fileName.txt,bucket=contractbucket]");
		
		String output = getDocument("contractbucket", "fileName.txt");
		
		// Assert document content.
		assertEquals("This is contract", output);
	}

	/**
	 * Assert if URL is not null.
	 * Assert if downloaded file from URL is correct.
	 * @throws IOException 
	 */
	@Test
	public void testGetTempLinkSuccessful() throws IOException {
		String url = contract.getTempLink("fileName.txt");
		
		// Assert URL not null.
		assertNotNull(url);
		
		URL downloadLink = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(downloadLink.openStream());
		FileOutputStream fos = new FileOutputStream("D:///fileName.txt");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		
		String fileName = "D:///fileName.txt";
		FileReader file = new FileReader(fileName);
		BufferedReader txtReader = new BufferedReader(file);
		String output = "";
		
		 while (true) {
			 String line = txtReader.readLine();
			 if (line == null) break;
			 output += line;			 
		 }
		
		 txtReader.close();
		 assertEquals("This is contract", output);
	}
	
	/**
	 * Get the document by bucketName and file name
	 * Store it to specified path
	 * @param bucketName
	 * @param key
	 * @throws IOException 
	 */
	private static String getDocument(String bucketName, String key) throws IOException {
		String output = null;
		S3Object object = null;
		
		try {
			
			object = s3.getObject(new GetObjectRequest(bucketName, key));
			output = readDocStream(object.getObjectContent());
			
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		return output;
	}
	

	/**
	 * Read and display the document content
	 * @param input
	 * @throws IOException
	 */
	private static String readDocStream(InputStream input) throws IOException {
		 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		 String output = "";
		 while (true) {
			 String line = reader.readLine();
			 if (line == null) break;
			 output += line;			 
		 }
		 
		 return output;
	}
}
