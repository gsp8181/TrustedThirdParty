package com.team2.jax.certificates;


import java.util.Date;

import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;



public class CertificateRepositoryDynamo implements CertificateRepository{
	
	private final static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	private final static DynamoDBMapper mapper = new DynamoDBMapper(client);	
	private final static String EMAIL="Email";
	private final static String TIME="Time"; 
	private final static String CERTIFICATE = "Certificate";	
	private final static ProvisionedThroughput THRUPUT = new ProvisionedThroughput(5L, 6L);	
	
	private static DynamoDB dynamo;
	static {
       try{
        
        dynamo= new DynamoDB(client);
        
        Table table = dynamo.getTable(CERTIFICATE);
        // check if table already exists, and if so wait for it to become active
        TableDescription desc = table.waitForActiveOrDelete();
        if (desc != null) {
            System.out.println("Skip creating table which already exists and ready for use: "
                    + desc);          
                 
        }
        
        // Table doesn't exist.  Let's create it.
        else{
        CreateTableRequest req = new CreateTableRequest()
        .withTableName(CERTIFICATE)
        .withAttributeDefinitions(
        		new AttributeDefinition(EMAIL,ScalarAttributeType.S),
        		new AttributeDefinition(TIME, ScalarAttributeType.N))
        .withKeySchema(
        		new KeySchemaElement(EMAIL,KeyType.HASH),
        		new KeySchemaElement(TIME, KeyType.RANGE))
        .withProvisionedThroughput(THRUPUT);
        
        
        table = dynamo.createTable(req);
        
        // Wait for the table to become active 
        desc = table.waitForActive();
        System.out.println("Table is ready for use! " + desc);
        }    
        
        
        
           
        
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }       
        
        }   
		
		



	public Certificate findByEmail(String email) {
		Certificate user = new Certificate();
		user.setEmail(email);
		DynamoDBQueryExpression<Certificate> queryExpression = new DynamoDBQueryExpression<Certificate>()
			    .withHashKeyValues(user).withScanIndexForward(false);
		List<Certificate> itemList = mapper.query(Certificate.class, queryExpression);
		
		if(itemList.isEmpty()){return null;}
		else {return itemList.get(0);}
		
	}

	public Certificate create(Certificate cert) {
		cert.setTime(new Date().getTime());
		mapper.save(cert);
		return cert;
	}

	
	
	



}
