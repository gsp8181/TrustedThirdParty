package com.team2.jax.certificates;

import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.validator.constraints.Email;
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
    private boolean status;
    private String code;
    
    @DynamoDBHashKey(attributeName="Email")
    @Email(message = "The email address must be in the format of name@domain.com")
    public String getEmail() { return email;}
    public void setEmail(String email) {this.email = email;}
    
   
    @DynamoDBRangeKey(attributeName="Time")  
    public long getTime(){return time;}
    public void setTime(long time){this.time=time;}
   
    @DynamoDBAttribute(attributeName="PublicKey")
    public String getPublicKey(){return publicKey;}
    public void setPublicKey(String publicKey){this.publicKey=publicKey;}
    
    @DynamoDBAttribute(attributeName="Status")
    public boolean getStatus(){	   return status;   	}
    public void setStatus(boolean status){this.status=status;}
    
    @DynamoDBAttribute(attributeName="Code")
    public String getCode(){return code;}
    public void setCode(String code){this.code=code;}
    
    
    
   
}
