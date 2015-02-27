package com.team2.jax.certificates;



import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@XmlRootElement
@DynamoDBTable(tableName="Certificate")
public class Certificate {

	private String email;
    private String publicKey;
    private long time;
    
    @DynamoDBHashKey(attributeName="Email")  
    public String getEmail() { return email;}
    public void setEmail(String email) {this.email = email;}
    
   
    @DynamoDBRangeKey(attributeName="Time")  
    public long getTime(){return time;}
    public void setTime(long time){this.time=time;}
   
    @DynamoDBAttribute(attributeName="PublicKey")
    public String getPublicKey(){return publicKey;}
    public void setPublicKey(String publicKey){this.publicKey=publicKey;}
    
	
   
}
