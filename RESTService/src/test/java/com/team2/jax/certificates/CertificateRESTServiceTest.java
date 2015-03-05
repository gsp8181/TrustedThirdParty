package com.team2.jax.certificates;

import static com.jayway.restassured.RestAssured.*;
//import groovyx.net.http.ContentType;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

//import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.*;
import com.team2.jax.certificates.CertificateRESTService;

//import org.junit.BeforeClass;
import org.junit.Test;

public class CertificateRESTServiceTest {
	
	static CertificateRESTService cf = new CertificateRESTService();				
	
	@Test
	public void getCertByUsernameTest() {
		cf.getCertByUsername("rayearly@gmail.com");
		
		Response res = get("http://localhost:8080/service/rest/certificates/rayearly@gmail.com");
		
		String json = res.asString();
		
		JsonPath jp = new JsonPath(json);
		
		
		assertEquals("rayearly@gmail.com", jp.get("email"));
	}
	

	/*@Test
	public void sendCertTest() {
		assertNull(null);
	}*/
	
	/*@Test
	public void verifyEmailTest() {
		assertNull(null);
	}*/
	
}
