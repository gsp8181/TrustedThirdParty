package com.team2.jax.contract;

import static org.junit.Assert.*;

import org.junit.Test;

public class ContractDynamoTest {
	private static ContractRepositoryDynamo crud =new ContractRepositoryDynamo();
	private static String RECIPIENT = "json@gmail.com"; 

	@Test
	public void testCreate() {
		Contract c = new Contract();
		c.setRecipient(RECIPIENT);
		c.setCompleted(false);
		crud.create(c);
		assertNotNull(crud.getById(c.getId()));
		assertFalse(crud.getById(c.getId()).isCompleted());
		
		c.setCompleted(true);
		crud.create(c);
		assertTrue(crud.getById(c.getId()).isCompleted());		
		
		crud.deleteById(c.getId());
		assertNull(crud.getById(c.getId()));
		
	}

	@Test
	public void testGetUnsignedContract() {
		assertTrue(crud.getUnsignedContractsByRecipient(RECIPIENT).isEmpty());
		
		Contract contract0 = new Contract();
		contract0.setCompleted(false);
		contract0.setRecipient(RECIPIENT);
		Contract contract1 = new Contract();
		contract1.setCompleted(false);
		contract1.setRecipient(RECIPIENT);		
	
		crud.create(contract0);
		crud.create(contract1);
		assertEquals(2,crud.getUnsignedContractsByRecipient(RECIPIENT).size());
		
		contract0.setCompleted(true);
		crud.create(contract0);
		assertEquals(1,crud.getUnsignedContractsByRecipient(RECIPIENT).size());
		assertEquals(contract1.getId(),crud.getUnsignedContractsByRecipient(RECIPIENT).get(0).getId());
		
		crud.deleteById(contract0.getId());
		crud.deleteById(contract1.getId());
		
		assertTrue(crud.getUnsignedContractsByRecipient(RECIPIENT).isEmpty());
	}

}
