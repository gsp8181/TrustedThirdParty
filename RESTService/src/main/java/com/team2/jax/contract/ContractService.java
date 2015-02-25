package com.team2.jax.contract;

import com.team2.jax.contract.input.StartSign;


public class ContractService {

	private static ContractValidator validator = new ContractValidator();
	
	private static ContractRepository cod = new ContractRepositoryMemory();
	
	public Contract start(StartSign ssObj) throws Exception {
		validator.validate(ssObj);

		Contract c = new Contract();
		
		c.setIntermediateContract(ssObj.getSig());
		c.setSender(ssObj.getUsername());
		c.setRecipient(ssObj.getRecipient());
		
		return cod.create(c);
		
		
	}

}
