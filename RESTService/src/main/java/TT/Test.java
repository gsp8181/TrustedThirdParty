package TT;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.team2.jax.contract.Contract;
import com.team2.jax.contract.ContractRepositoryDynamo;

public class Test {
	public static void main(String[] args) throws InterruptedException
	{
//		ContractRepositoryDynamo tt= new 		ContractRepositoryDynamo();
//		Contract c= new Contract();
//		
//		c.setContract("");
//		c.setDocName("");
//		c.setDocRef("");
//		c.setRecipient("123");
//		c.setIntermediateContract("");
//		c.setRecipient("");
//		tt.create(c);	
		
//		tt.getUnsignedContractsByRecipient("de18ad16-1fd0-4c4e-87d1-ccc11581f76f");
		
		
		
		
		
		
		
		DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
			    new ProfileCredentialsProvider()));

			Table table = dynamoDB.getTable("Contract");

			table.delete();

			table.waitForDelete();
		
		
		
		
		
		
	}

	
}
