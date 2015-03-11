package com.team2.jax.certificates;

import static com.jayway.restassured.RestAssured.*;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CertificateRESTServiceTest {
	
	private static CertificateRepositoryDynamo crud = new CertificateRepositoryDynamo();
	private static Certificate c = new Certificate();
		
	@Before
	public void setUp() throws Exception {
		c.setEmail("Z.Zhong4@newcastle.ac.uk");
		c.setCode(UUID.randomUUID().toString());
		c.setTime(new Date().getTime());
		c.setPublicKey("MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAXDLqazK84fCxvIVBPJmVrattVLNky27leHI9tWBXZZAl1BpgRnBDNkgcTyoPh4tkh4jG+OEWAeN5Fi8vBWb/tCTLju8U2BMtwoQ9suZNySN5spHkGnV5fMIf8e1nFjf8MWOctW7+2b+Mh2LlfAZvpwityw4ZwrYIP0RnEFBRbEA=");
		c.setStatus(false);
		crud.create(c);
	}

	@After
	public void tearDown() throws Exception {
		crud.delete(c);
	}

	@Test
	public void getCertByUsernameTest() {		
		
		Response unverified = get("http://localhost:8080/service/rest/certificates/"+c.getEmail());
		assertEquals(404,unverified.getStatusCode());
		
		crud.verify(c.getEmail(), c.getCode());
		Response bob = get("http://localhost:8080/service/rest/certificates/"+c.getEmail());	
		assertEquals(200, bob.getStatusCode());
		Certificate cert = bob.as(Certificate.class);
		assertEquals(cert.getEmail(),c.getEmail());
		assertEquals(cert.getCode(),c.getCode());
		assertEquals(cert.getPublicKey(), c.getPublicKey());
					
		Response john = get("http://localhost:8080/service/rest/certificates/John@163.com");
		assertEquals(404, john.getStatusCode());
		
		
	
	}
	
	@Test
	public void sendCertTest() {		
		crud.verify(c.getEmail(), c.getCode());

		String json = "{\"publicKey\":\""+c.getPublicKey()+"\",\"email\":\""+c.getEmail()+"\",\"signedData\":\"MCwCFDIupSUpQDAKyJNgMAGnSxg2ht9bAhRf+sfmFHuHmyiHbLKaPdbloERZCg==\"}";
		
        Response resend = postResult(json);
		assertEquals(resend.getStatusCode(),400);
		assertEquals(resend.as(HashMap.class).get("username"),"Username Already Exists");
		
		crud.delete(c);		
		
		Response signedData = postResult("{\"publicKey\":\""+c.getPublicKey()+"\",\"email\":\"bob@email.com\",\"signedData\":\"MCwCFDIupSUpQDAKyJNgMAGnSxg2ht9bAhRf+sfmFHuHmyiHbLKaPdbloERZCg==\"}");
		assertEquals(400, signedData.getStatusCode());
		assertEquals(signedData.as(HashMap.class).get("signedData"),"Certificate verification failed");
				
		Response email = postResult("{\"publicKey\":\""+c.getPublicKey()+"\",\"email\":\"bob\",\"signedData\":\"MCwCFDIupSUpQDAKyJNgMAGnSxg2ht9bAhRf+sfmFHuHmyiHbLKaPdbloERZC==\"}");
		assertEquals(400, email.getStatusCode());
		assertEquals(email.as(HashMap.class).get("email"),"The email address must be in the format of name@domain.com");
		
		Response validationFailure= postResult("{\"publicKey\":\""+c.getPublicKey()+"\",\"email\":\""+c.getEmail()+"\",\"signedData\":\"MCwCFDIupSUpQDAKyJNgMAGnSxg2ht9bAhRf+sfmFHuHmyiHbLKaPdbloERZC==\"}");
		assertEquals(500, validationFailure.getStatusCode());
		
		Response bob = postResult(json);		
		assertEquals(bob.getStatusCode(),201);
		Certificate cert = bob.as(Certificate.class);
		assertEquals(cert.getEmail(), c.getEmail());
		assertEquals(cert.getPublicKey(), c.getPublicKey());
		assertEquals(cert.getCode(), "Certificate not verified, check your emails to verify this certificate");
		
		
				
		crud.delete(cert);		
		
	}
	
    @Test
	public void verifyEmailTest() {
    
		Response bob = get("http://localhost:8080/service/rest/certificates/verify?email="+c.getEmail()+"&code="+c.getCode());
		assertEquals(200, bob.getStatusCode());
		
		bob = get("http://localhost:8080/service/rest/certificates/verify?email="+c.getEmail()+"&code=vsfafqa");
		assertEquals(400, bob.getStatusCode());
	}
    
    
    private Response postResult(String json)
    {
    	return  given().
				contentType(ContentType.JSON).
				body(json).
				post("http://localhost:8080/service/rest/certificates/");	
    }
	
}
