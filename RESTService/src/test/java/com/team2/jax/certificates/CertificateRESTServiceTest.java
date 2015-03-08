package com.team2.jax.certificates;

import static com.jayway.restassured.RestAssured.*;

import java.util.Date;
import java.util.UUID;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

//import javax.ws.rs.core.Response.Status;







import static org.junit.Assert.*;

import com.team2.jax.certificates.CertificateRESTService;







import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
//import org.junit.BeforeClass;
import org.junit.Test;

public class CertificateRESTServiceTest {
	
	private CertificateRepositoryDynamo crud = new CertificateRepositoryDynamo();
	private Certificate c = new Certificate();
		
	@Before
	public void setUp() throws Exception {
		c.setEmail("bob@163.com");
		c.setCode(UUID.randomUUID().toString());
		c.setTime(new Date().getTime());
		c.setPublicKey("763920bf-51b3-4f11-b67a-d7f308d03525");
		c.setStatus(false);
		crud.create(c);
	}

	@After
	public void tearDown() throws Exception {
		crud.delete(c);
	}

	@Test
	public void getCertByUsernameTest() {		
		
		Response unverified = get("http://localhost:8080/service/rest/certificates/bob@163.com");
		assertEquals(404,unverified.getStatusCode());
		
		crud.verify(c.getEmail(), c.getCode());
		Response bob = get("http://localhost:8080/service/rest/certificates/bob@163.com");	
		assertEquals(200, bob.getStatusCode());
		assertEquals("bob@163.com",new JsonPath(bob.asString()).getString("email"));
		
		Response john = get("http://localhost:8080/service/rest/certificates/John@163.com");
		assertEquals(404, john.getStatusCode());
		
		
	
	}
	
	@Test
	public void sendCertTest() {
		
	}
	
    @Test
	public void verifyEmailTest() {
    
		Response bob = get("http://localhost:8080/service/rest/certificates/verify?email=bob@163.com&code="+c.getCode());
		assertEquals(200, bob.getStatusCode());
		
		bob = get("http://localhost:8080/service/rest/certificates/verify?email=bob@163.com&code=vsfafqa");
		assertEquals(400, bob.getStatusCode());
	}
	
}
