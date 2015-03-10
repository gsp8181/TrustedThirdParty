package com.team2.jax.contract;

import static com.jayway.restassured.RestAssured.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import static org.junit.Assert.*;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateRepositoryDynamo;
import com.team2.jax.contract.objects.ContractComplete;
import com.team2.jax.contract.objects.ContractDoc;
import com.team2.jax.contract.objects.ContractIntermediate;
import com.team2.security.CertificateTools;
import com.team2.security.TimeStampedKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContractRESTServiceTest {
	private static CertificateRepositoryDynamo crud = new CertificateRepositoryDynamo();
	private static Certificate c = new Certificate();
	private static ContractRepositoryDynamo crd =new ContractRepositoryDynamo();
	private PrivateKey key; 
	private static TimeStampedKey t;
		
	@Before
	public void setUp() throws Exception {
		c.setEmail("Z.Zhong4@newcastle.ac.uk");
		c.setCode(UUID.randomUUID().toString());
		c.setTime(new Date().getTime());
		c.setPublicKey("MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGANpbYtHLZ4bRvwGiEeksKjFKB7RvHuSPzf5qJwGlsz9bCFXocM6w0EVl7EumkEH3Mbi955iC9IimHDwPMaXirD9WPLkqRherc4gWyEv1ojDhJG6uQl9Wfg19QQ2LpNXenYGfuQb04aXqEOPyQmjiUNlw99RxQKj/L9s3mNlROrfk=");
		c.setStatus(true);
		crud.create(c);		
		c.setEmail("Z.Zhong4@ncl.ac.uk");
		c.setPublicKey("MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAXDLqazK84fCxvIVBPJmVrattVLNky27leHI9tWBXZZAl1BpgRnBDNkgcTyoPh4tkh4jG+OEWAeN5Fi8vBWb/tCTLju8U2BMtwoQ9suZNySN5spHkGnV5fMIf8e1nFjf8MWOctW7+2b+Mh2LlfAZvpwityw4ZwrYIP0RnEFBRbEA=");
		crud.create(c);		
		key = CertificateTools.decodeDSAPriv("MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUF4FdYLxKu3s1WnUmN+uNpiX+sOU=");
		t= CertificateTools.genTimestamp(key);
	}

	@After
	public void tearDown() throws Exception {
		crud.delete(c);
		c.setEmail("Z.Zhong4@newcastle.ac.uk");
		crud.delete(c);
	}


	@Test
	public void Step1() {		
		String json0 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4\", \"recipient\":\"Z.Zhong4\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response email = given().
				contentType(ContentType.JSON).
				body(json0).
				post("http://localhost:8080/service/rest/contracts/1/");	
		assertEquals(400, email.getStatusCode());
		HashMap<String, String> error = email.as(HashMap.class);
		assertEquals(error.get("recipient"),"The email address must be in the format of name@domain.com");
		assertEquals(error.get("email"),"The email address must be in the format of name@domain.com");
		
		String json1 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"sender@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@ncl.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response sender = given().
				contentType(ContentType.JSON).
				body(json1).
				post("http://localhost:8080/service/rest/contracts/1/");	
		assertEquals(400,sender.getStatusCode());
		assertEquals(sender.as(HashMap.class).get("username"),"No certificate was found for the designated sender");
		
		String json2 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"recipient@ncl.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response recipient = given().
				contentType(ContentType.JSON).
				body(json2).
				post("http://localhost:8080/service/rest/contracts/1/");	
		assertEquals(recipient.getStatusCode(),400);
		assertEquals(recipient.as(HashMap.class).get("recipient"),"No certificate was found for the designated recipient, please check the name or tell them to register with the service");
		
		String json3 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@ncl.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response sig = given().
				contentType(ContentType.JSON).
				body(json3).
				post("http://localhost:8080/service/rest/contracts/1/");	
		assertEquals(400, sig.getStatusCode());
		assertEquals(sig.as(HashMap.class).get("sig"),"Validation of the signature failed, make sure the signing key is the database");
				
		String json4 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQ=\"}";
		Response wrongSig = given().
				contentType(ContentType.JSON).
				body(json4).
				post("http://localhost:8080/service/rest/contracts/1/");
		assertEquals(500, wrongSig.getStatusCode());	
		
		String json5 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response res = given().
				contentType(ContentType.JSON).
				body(json5).
				post("http://localhost:8080/service/rest/contracts/1/");
		assertEquals(res.getStatusCode(),200);
		ContractIntermediate newContract = res.as(ContractIntermediate.class);
		assertEquals(newContract.getRecipient(),"Z.Zhong4@newcastle.ac.uk");
		assertEquals(newContract.getSender(),"Z.Zhong4@newcastle.ac.uk");		
		
		crd.deleteById(newContract.getId());
			
	}
	@Test
	public void deleteRecord() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException{
		String json0 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response res = given().
				contentType(ContentType.JSON).
				body(json0).
				post("http://localhost:8080/service/rest/contracts/1/");
		ContractIntermediate newContract = res.as(ContractIntermediate.class);

		Response res1 = delete("http://localhost:8080/service/rest/contracts/abort/"+newContract.getId()+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(res1.getStatusCode(),204);
	}
	
	@Test
	public void Step2() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		Response res0 = get("http://localhost:8080/service/rest/contracts/2/Z.Zhong4@newcastle.ac.uk?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(res0.getStatusCode(), 404);
		
		String json0 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response res1 = given().
				contentType(ContentType.JSON).
				body(json0).
				post("http://localhost:8080/service/rest/contracts/1/");
		ContractIntermediate newContract = res1.as(ContractIntermediate.class);
		
		Response record = get("http://localhost:8080/service/rest/contracts/2/Z.Zhong4@newcastle.ac.uk?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(record.getStatusCode(),200);

		crd.deleteById(newContract.getId());
		
	}
	
    @Test
	public void Step3() {
    	String json0 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response res0 = given().
				contentType(ContentType.JSON).
				body(json0).
				post("http://localhost:8080/service/rest/contracts/1/");
		ContractIntermediate newContract = res0.as(ContractIntermediate.class);
		

		String json1 = "{\"sig\":\"MC0CFQCQ8HoCEaEisq/YPIy9RGMBkDbyDgIUEes7qkq4j0v7fKRlKf5BKqTL+V0=\"}";
		Response sign = given().
				contentType(ContentType.JSON).
				body(json1).
				post("http://localhost:8080/service/rest/contracts/3/"+newContract.getId());
		assertEquals(sign.getStatusCode(), 200);
		
		Response res1 = given().
				contentType(ContentType.JSON).
				body(json1).
				post("http://localhost:8080/service/rest/contracts/3/"+newContract.getId());
		assertEquals(res1.getStatusCode(), 400);
		
		crd.deleteById(newContract.getId());
    
	
	}
	
    @Test
    public void Step4(){
    	Response res0 = get("http://localhost:8080/service/rest/contracts/4/1283192736812736918273?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(res0.getStatusCode(),404);
    	
    	String json0 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response res1 = given().
				contentType(ContentType.JSON).
				body(json0).
				post("http://localhost:8080/service/rest/contracts/1/");
		ContractIntermediate newContract = res1.as(ContractIntermediate.class);
		
		Response res2 = get("http://localhost:8080/service/rest/contracts/4/"+newContract.getId()+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(res2.getStatusCode(),403);
		

		String json1 = "{\"sig\":\"MC0CFQCQ8HoCEaEisq/YPIy9RGMBkDbyDgIUEes7qkq4j0v7fKRlKf5BKqTL+V0=\"}";
		Response sign = given().
				contentType(ContentType.JSON).
				body(json1).
				post("http://localhost:8080/service/rest/contracts/3/"+newContract.getId());
		ContractDoc result = sign.as(ContractDoc.class);
			
		Response doc = get("http://localhost:8080/service/rest/contracts/4/"+newContract.getId()+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(doc.getStatusCode(),200);
		ContractDoc record =doc.as(ContractDoc.class); 
		assertEquals(record.getDocRef(),result.getDocRef());
		
		crd.deleteById(newContract.getId());
    	
    }
    
    @Test
    public void Step5(){
    	Response res0 = get("http://localhost:8080/service/rest/contracts/5/1283192736812736918273?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(res0.getStatusCode(),404);
    	
    	String json0 = "{\"docData\":\"SGVsbG8sIHdvcmxkIQ==\", \"docName\":\"hello.txt\", \"email\":\"Z.Zhong4@newcastle.ac.uk\", \"recipient\":\"Z.Zhong4@newcastle.ac.uk\", \"sig\":\"MC0CFAPyr9ic9zrECcV2Yl9+4Ho8hHSwAhUAkMctO7r11lalzv0ExYn4czG/yQE=\"}";
		Response res1 = given().
				contentType(ContentType.JSON).
				body(json0).
				post("http://localhost:8080/service/rest/contracts/1/");
		ContractIntermediate newContract = res1.as(ContractIntermediate.class);
		
		Response res2 = get("http://localhost:8080/service/rest/contracts/5/"+newContract.getId()+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(res2.getStatusCode(),403);
		

		String json1 = "{\"sig\":\"MC0CFQCQ8HoCEaEisq/YPIy9RGMBkDbyDgIUEes7qkq4j0v7fKRlKf5BKqTL+V0=\"}";
		Response sign = given().
				contentType(ContentType.JSON).
				body(json1).
				post("http://localhost:8080/service/rest/contracts/3/"+newContract.getId());
		
			
		Response doc = get("http://localhost:8080/service/rest/contracts/5/"+newContract.getId()+"?ts="+t.getTime()+"&signedStamp="+t.getSignedKey());
		assertEquals(doc.getStatusCode(),200);
		ContractComplete record =doc.as(ContractComplete.class); 
		assertEquals(record.getSig(),"MC0CFQCQ8HoCEaEisq/YPIy9RGMBkDbyDgIUEes7qkq4j0v7fKRlKf5BKqTL+V0=");
		
		crd.deleteById(newContract.getId());
    }
	
}
