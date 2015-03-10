package com.team2.jax.certificates;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

public class CertificateDynamoTest {
	
	private static CertificateRepositoryDynamo crud = new CertificateRepositoryDynamo();	
	private static long TIME = new Date().getTime();
	private static final String EMAIL = "John@gmail.com";


	@Test
	public void testCreateNewCertificate() {
		Certificate c = new Certificate();
		c.setEmail(EMAIL);
		c.setTime(TIME);
		
		assertNull(crud.findCertificate(EMAIL, TIME));	
		
		crud.create(c);
	
		assertNotNull(crud.findCertificate(EMAIL, TIME));
		
		crud.delete(c);
		
		assertNull(crud.findCertificate(EMAIL,TIME));
	
	
	}

	@Test
	public void testVerify() {
		
        Certificate c = new Certificate();
        String code = UUID.randomUUID().toString();
        c.setEmail(EMAIL);
        c.setTime(TIME);
        c.setCode(code);
        c.setStatus(false);  	
		
		crud.create(c);
		
		assertFalse(crud.findCertificate(EMAIL, TIME).getStatus());
		
		assertFalse(crud.verify(EMAIL, "123"));
		
		assertFalse(crud.findCertificate(EMAIL, TIME).getStatus());
		
		assertTrue(crud.verify(EMAIL, code));
		
		assertTrue(crud.findCertificate(EMAIL,TIME).getStatus());
		
		crud.delete(crud.findCertificate(EMAIL, TIME));
		
		assertNull(crud.findCertificate(EMAIL,TIME));
		
	}
	
	@Test
	public void testFindLatestCertificate(){
		
		Certificate c0 = new Certificate();
		Certificate c1 = new Certificate();
		c0.setEmail(EMAIL);
		c0.setTime(TIME);
		c1.setEmail(EMAIL);
		c1.setTime(TIME+1L);
		
		
		assertNull(crud.findLatestByEmail(EMAIL));
		
		crud.create(c0);		
		assertEquals(TIME,crud.findLatestByEmail(EMAIL).getTime());
				
		crud.create(c1);
		assertEquals(TIME+1L,crud.findLatestByEmail(EMAIL).getTime());
		
		crud.delete(c0);
		crud.delete(c1);
	}
	
	@Test 
	public void testFindByEmail(){
		Certificate c0 = new Certificate();
		Certificate c1 = new Certificate();
		c0.setEmail(EMAIL);
		c0.setStatus(false);
		c0.setTime(TIME);
		c1.setEmail(EMAIL);
		c1.setTime(TIME+1L);
		c1.setStatus(false);
		
		assertNull(crud.findByEmail(EMAIL));
		
		crud.create(c0);
		assertNull(crud.findByEmail(EMAIL));
		
		c0.setStatus(true);
		crud.create(c0);
		assertEquals(TIME,crud.findByEmail(EMAIL).getTime());
		
		crud.create(c1);
		
		assertEquals(TIME,crud.findByEmail(EMAIL).getTime());
		
		c1.setStatus(true);
		crud.create(c1);
		assertEquals(TIME+1L,crud.findByEmail(EMAIL).getTime());
		
		crud.delete(c0);
		crud.delete(c1);
		
		
		
	}

}
