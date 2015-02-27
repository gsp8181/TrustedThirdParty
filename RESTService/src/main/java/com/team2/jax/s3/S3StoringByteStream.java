package com.team2.jax.s3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.SimpleMaterialProvider;

/**
 * Storing document of type bytearrayinputstream into amazonS3
 * @author b4064328
 *
 */
public class S3StoringByteStream {
	
	private static AWSCredentialsProviderChain credentials = null;
	private static AmazonS3EncryptionClient s3 = null;
	
	public static void main(String [] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		// Create connection
		encyrptedConnCreation();
		
		// Create/list/delete bucket.
		bucketManagement();
		
		// Create a doc in text to be stored in text file later in storing function
		//ByteArrayInputStream docstream = new ByteArrayInputStream("This is the first document created for test.".getBytes());
		String bucketName = "firsttestedbucket";
		String key = "firsttest.txt";
		
		// Store the doc in s3 - bytearrayinputstream type
		storeObject(key, bucketName);
		
		
		
		// Get the object by bucketName
		getDocument(bucketName, key);
		
		//deleteObject(bucketName, key);
		//deleteBucket(bucketName);
	}
	
	/**
	 * Create Connection on eu_west_1 (said to be the cheapest)
	 * Save encryption key pair somewhere.
	 * After created, just use the encryption client normally.
	 * when use the putObject method, data in the file or InputStream is encrypted as its uploaded.
	 * same goes when retrieve with getObject.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws NoSuchProviderException 
	 */
	private static void encyrptedConnCreation() throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		System.out.println("Create connection...");

		
		// Randomly generated keyPair - TODO: create a key pair and store it somewhere
		// so it wont lost when JVM exits.
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		KeyPair myKeyPair = keyGenerator.generateKeyPair();
		
		
		// To set this on a non EC2 instance, run http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
		//credentials = new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),new ClasspathPropertiesFileCredentialsProvider());
		credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
		EncryptionMaterialsProvider encryptionMaterials =  new SimpleMaterialProvider().addMaterial(new EncryptionMaterials(myKeyPair));
		s3 = new AmazonS3EncryptionClient(credentials, encryptionMaterials);
		Region euIreland = Region.getRegion(Regions.EU_WEST_1);
		s3.setRegion(euIreland);
		System.out.println();
	}
	
	/**
	 * Create/list bucket with name of "firsttestedbucket"
	 * @throws IOException
	 */
	private static void bucketManagement() throws IOException {
		System.out.println("Starting bucket management...");
		String bucketName = "firsttestedbucket";
		
		try {
			System.out.println("Create bucket...");
			// Create bucket
			//s3.createBucket(bucketName);
			
			// List bucket currently in account
			System.out.println("Listing buckets in account: ");
            for (Bucket bucket : s3.listBuckets()) {
                System.out.println(" - " + bucket.getName());
            }
            System.out.println();
			
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
	}
	
	/**
	 * Delete bucket
	 * @throws IOException
	 */
	private static void deleteBucket(String bucketName) throws IOException {
		try {
            
            // Delete bucket in account
            System.out.println("Delete bucket...");
			s3.deleteBucket(bucketName);
			
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
	}
	
	/**
	 * Store the doc in type of bytearraystream into s3
	 * Create a file firsttest.txt with string from the doc
	 * @param doc
	 * @throws IOException 
	 */
	private static void storeObject(String key, String bucketName) throws IOException { // previously had ByteArrayInputStream doc param
		try {
			System.out.println("Store doc in s3...");
			// put object of firsttest textfile into s3 containing the words from the doc bytearrayinputstream
			//s3.putObject(bucketName, key, doc, new ObjectMetadata());
			
			s3.putObject(new PutObjectRequest(bucketName, key, createSampleDoc()));
			// Change object ACL
			//s3.setObjectAcl(bucketName, "firsttest.txt", CannedAccessControlList.Private); //or PublicRead
			System.out.println();
			
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
	}
	
	/**
	 * Delete Object
	 * @param bucketName
	 */
	private static void deleteObject(String bucketName, String key) {
		try {
			// Delete object with bucketName
			System.out.println("Delete object in bucket "+ bucketName);
			s3.deleteObject(bucketName, key);
			
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
	}
	
	/**
	 * Get the document by bucketName and file name
	 * Store it to specified path
	 * @param bucketName
	 * @param key
	 * @throws IOException 
	 */
	private static void getDocument(String bucketName, String key) throws IOException {
		try {
			System.out.println("Download the document to specified path..");
			s3.getObject(new GetObjectRequest(bucketName, key), new File("D:///firsttest.txt"));
			System.out.println("Display document content..");
			S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
			readDocStream(object.getObjectContent());
			
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
	}

	/**
	 * Create temporary sample file to store into s3
	 * @return temporary file
	 * @throws IOException
	 */
	private static File createSampleDoc() throws IOException {
		File file = File.createTempFile("firsttest", ".txt");
		file.deleteOnExit();
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("This is first document");
        writer.write("to store to amazon s3");
		writer.write("thank you very much.");
		writer.close();
		
		return file;
	}	
	
	/**
	 * Read and display the document content
	 * @param input
	 * @throws IOException
	 */
	private static void readDocStream(InputStream input) throws IOException {
		 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        while (true) {
	            String line = reader.readLine();
	            if (line == null) break;
	            System.out.println("    " + line);
	        }
	        System.out.println();
	}
}
