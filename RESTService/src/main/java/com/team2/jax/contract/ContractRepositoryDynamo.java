package com.team2.jax.contract;

import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class ContractRepositoryDynamo implements ContractRepository {

	private final static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	private final static DynamoDBMapper mapper = new DynamoDBMapper(client);	
	private final static String ID="Id";
	private final static String RECIPIENT="Recipient";
	private final static String STATUS="Status";
	private final static Projection PROJECTION = new Projection().withProjectionType(ProjectionType.ALL);
	private final static String CONTRACT = "Contract";	
	private final static ProvisionedThroughput THRUPUT = new ProvisionedThroughput(5L, 6L);	
	
	private static DynamoDB dynamo;
	static {
       try{
        
        dynamo= new DynamoDB(client);
        
        Table table = dynamo.getTable(CONTRACT);
        // check if table already exists, and if so wait for it to become active
        TableDescription desc = table.waitForActiveOrDelete();
        if (desc != null) {
            System.out.println("Skip creating table which already exists and ready for use: "
                    + desc);          
                 
        }
        
        // Table doesn't exist.  Let's create it.
        else{
        CreateTableRequest req = new CreateTableRequest()
        .withTableName(CONTRACT)
        .withAttributeDefinitions(
        		new AttributeDefinition(ID,ScalarAttributeType.S),
        		new AttributeDefinition(RECIPIENT,ScalarAttributeType.S),
        		new AttributeDefinition(STATUS, ScalarAttributeType.B))
        .withKeySchema(
        		new KeySchemaElement(ID,KeyType.HASH))
        .withProvisionedThroughput(THRUPUT)
        .withGlobalSecondaryIndexes(
        		new GlobalSecondaryIndex()
        		   .withIndexName(RECIPIENT)
        		   .withKeySchema(
        				   new KeySchemaElement(RECIPIENT,KeyType.HASH),
        				   new KeySchemaElement(STATUS,KeyType.RANGE))
        			.withProjection(PROJECTION)
        			.withProvisionedThroughput(THRUPUT));
        				   
        		 
        
        
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
	@Override
	public Contract create(Contract c) {
		c.setCompleted(false);
		mapper.save(c);
		System.out.println(c.getId());
		return c;
	}

	@Override
	public List<Contract> getUnsignedContractsByRecipient(String recipient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contract getById(String id) {
		return mapper.load(Contract.class, id);
	}

}
