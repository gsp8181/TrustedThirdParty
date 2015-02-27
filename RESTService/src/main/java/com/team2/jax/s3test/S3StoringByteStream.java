package com.team2.jax.s3test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

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
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Storing document of type bytearrayinputstream into amazonS3
 * @author b4064328
 *
 */
public class S3StoringByteStream {
	
	private static AWSCredentialsProviderChain credentials = null;
	private static AmazonS3 s3 = null;
	
	public static void main(String [] args) throws IOException {
		// Create connection
		connCreation();
		
		// Create/list/delete bucket.
		bucketManagement();
		
		// Create a doc in text to be stored in text file later in storing function
		ByteArrayInputStream docstream = new ByteArrayInputStream("This is the first document created for test.".getBytes());
		String bucketName = "firsttestedbucket";
		String key = "firsttest.txt";
		
		// Store the doc in s3
		storeObject(docstream, key, bucketName);
		
		// Get the object by bucketName
		getDocument(bucketName, key);
		
		//deleteObject(bucketName, key);
		//deleteBucket();
	}
	
	/**
	 * Create credential for accessing
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static void clientConfiguration() throws IOException {
		//credentials = new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),new ClasspathPropertiesFileCredentialsProvider());
		credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
		
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTP);
		
		AmazonS3 s3 = new AmazonS3Client(credentials, clientConfig);
	}
	
	/**
	 * Create Connection on eu_west_1 (said to be the cheapest)
	 * @throws IOException
	 */
	private static void connCreation() throws IOException {
		System.out.println("Create connection...");
		
		//credentials = new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),new ClasspathPropertiesFileCredentialsProvider());
		credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
		s3 = new AmazonS3Client(credentials);
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
			s3.createBucket(bucketName);
			
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
	private static void deleteBucket() throws IOException {
		System.out.println("Starting bucket management...");
		String bucketName = "firsttestedbucket";
		
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
	private static void storeObject(ByteArrayInputStream doc, String key, String bucketName) throws IOException {
		try {
			System.out.println("Store doc in s3...");
			// put object of firsttest textfile into s3 containing the words from the doc bytearrayinputstream
			s3.putObject(bucketName, key, doc, new ObjectMetadata());
			
			// Change object ACL
			//s3.setObjectAcl(bucketName, "firsttest.txt", CannedAccessControlList.Private); //or PublicRead
			
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
	 */
	private static void getDocument(String bucketName, String key) {
		try {
			s3.getObject(new GetObjectRequest(bucketName, key), new File("D:///firsttest.txt"));
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
	
	// Do byte stream one maybe
}
