package com.team2.jax.certificates;

import java.util.List;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;



public class CertificateRepositoryDynamo implements CertificateRepository{
	
	private final static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain());
	private final static Region euIreland = Region.getRegion(Regions.EU_WEST_1);
	private final static DynamoDBMapper mapper = new DynamoDBMapper(client);	
	private final static String EMAIL="Email";
	private final static String TIME="Time"; 
	private final static String CERTIFICATE = "Certificate";	
	private final static ProvisionedThroughput THRUPUT = new ProvisionedThroughput(5L, 6L);	
	
	private static DynamoDB dynamo;
	static {
       try{
        
    	client.setRegion(euIreland);
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
		
		if(!itemList.isEmpty())
		{
			for(Certificate c:itemList)
			{
				if(c.getStatus()==true){return c;}
			}
		}

        return null;
		
	}
	
	
	public Certificate findLatestByEmail(String email)
	{
		Certificate user = new Certificate();
		user.setEmail(email);
		DynamoDBQueryExpression<Certificate> queryExpression = new DynamoDBQueryExpression<Certificate>()
			    .withHashKeyValues(user).withScanIndexForward(false);
		List<Certificate> itemList = mapper.query(Certificate.class, queryExpression);
		
		if(itemList.isEmpty()){return null;}

        return itemList.get(0);
		
	}

	public void create(Certificate cert) {
		
		mapper.save(cert);
		
	}
	
	
	
	public Certificate findCertificate(String email, long time)
	{
		Certificate user = new Certificate();
		user.setEmail(email);
		
		Condition condition = new Condition()
		        .withComparisonOperator(ComparisonOperator.EQ.toString())
		        .withAttributeValueList(new AttributeValue().withN(String.valueOf(time)));
		
		
		DynamoDBQueryExpression<Certificate> queryExpression = new DynamoDBQueryExpression<Certificate>()
			    .withHashKeyValues(user)
			    .withRangeKeyCondition("Time", condition);
		
		List<Certificate> list = mapper.query(Certificate.class, queryExpression);
		
		if(list.isEmpty()){return null;}
		
		return list.get(0);
		
			    
			    
	}
	
	
	public boolean verify(String email, String code)
	{
		Certificate latest = findLatestByEmail(email);
		if(latest!=null&&latest.getCode().equals(code))
		{
			latest.setStatus(true);
			mapper.save(latest);
			return true;			
		}
		
		return false;
		
	}
	
	
	public void delete(Certificate c)
	{
		mapper.delete(c);
	}

	
	
	



}
