package com.team2.jax.contract;


import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

public class ContractFileStoreS3 implements ContractFileStore {
	
	private static AWSCredentialsProviderChain credentials = null;
	private static AmazonS3 s3 = null;
	
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
	protected static AmazonS3 connCreation() throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		System.out.println("Create connection...");
		
		// To set this on a non EC2 instance, run http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
		//credentials = new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider(),new ClasspathPropertiesFileCredentialsProvider());
		credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
	
		s3 = new AmazonS3Client(credentials);
		//s3 = new AmazonS3EncryptionClient(credentials, encryptionMaterials);
		
		Region euIreland = Region.getRegion(Regions.EU_WEST_1);
		s3.setRegion(euIreland);
		System.out.println();
		
		return s3;
	}
	
	/**
	 * Create/list bucket with name of "contractbucket"
	 * if already exist then notify.
	 * @return 
	 * @throws IOException
	 */
	private static String bucketManagement() throws IOException {
		System.out.println("Create bucket...");
		String bucketName = "contractbucket";
		
		try {
			for (Bucket bucket : s3.listBuckets()) {
				if(bucketName.equalsIgnoreCase(bucket.getName())) {
					// Bucket exist
					System.out.println("Bucket already created.");
					break;
				} else {
					System.out.println("Create " + bucketName + ".");
					// Create bucket
					s3.createBucket(bucketName);
					System.out.println("Bucket created.");
				}
			}
			
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
		
		return bucketName;
	}
	
	public String saveFile(String fileName, byte[] doc) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		connCreation();
		
		System.out.println("Store document " + fileName + ".");

		byte[] contentBytes = null;
		String bucketName = bucketManagement();
		
		try {
			// Convert byte to InputStream to store to s3
			InputStream is = new ByteArrayInputStream(doc);
			contentBytes = IOUtils.toByteArray(is);
		} catch (IOException e) {
			System.err.printf("Failed while reading bytes from %s", e.getMessage());
		}
		
		Long contentLength = Long.valueOf(contentBytes.length);
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(contentLength);
		
		InputStream stream = new ByteArrayInputStream(doc);
		
		try {
			// Store object into bucket
			s3.putObject(new PutObjectRequest(bucketName, fileName, stream, metadata));
			System.out.println("File saved.");
			
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
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return fileName;
	}

	public String getTempLink(String identifier) {
		String bucketName = "contractbucket";
		String fileName = identifier;
		String url = "";
		
		//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		
		try {
			
			java.util.Date expiration = new java.util.Date();
			long msec = expiration.getTime();
			// msec += 1000 * 60 * 1 // 1 minute
			msec += 1000 * 60 * 5; // 5 minutes.
			expiration.setTime(msec);
			
			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
					new GeneratePresignedUrlRequest(bucketName, fileName);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(expiration);
			
			URL s = s3.generatePresignedUrl(generatePresignedUrlRequest); 
			url = s.toString();
			
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
		
		return url;
	}

	
}
