package com.team2.jax.contract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
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
import com.team2.jax.certificates.Certificate;

public class ContractRepositoryDynamo implements ContractRepository {

	private final static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
	private final static DynamoDBMapper mapper = new DynamoDBMapper(client);	
	private final static String ID="Id";
	private final static String RECIPIENT="Recipient";
	private final static String INDEX="Receiver";
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
        		new AttributeDefinition(RECIPIENT,ScalarAttributeType.S))
        .withKeySchema(
        		new KeySchemaElement(ID,KeyType.HASH))
        .withProvisionedThroughput(THRUPUT)
        .withGlobalSecondaryIndexes(
        		new GlobalSecondaryIndex()
        		   .withIndexName(INDEX)
        		   .withKeySchema(
        				   new KeySchemaElement(RECIPIENT,KeyType.HASH))
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
	
	private final static Table table = dynamo.getTable("Contract");
	private final static Index index = table.getIndex(INDEX);
	
	
	
	@Override
	public Contract create(Contract c) {
		c.setCompleted(false);
		mapper.save(c);
		System.out.println(c.getId());
		return c;
	}

	@Override
	public List<Contract> getUnsignedContractsByRecipient(String recipient) {
		List<Contract> list = new ArrayList<Contract>();
		
		ItemCollection<QueryOutcome> items = index.query(RECIPIENT,recipient);
		
		for(Item i:items)
		{
			if(i.getString("Status").equals("0")){list.add(getById(i.getString("Id")));}
		}		
				
		return list;
	}

	@Override
	public Contract getById(String id) {		

		return mapper.load(Contract.class, id);
	}

}
