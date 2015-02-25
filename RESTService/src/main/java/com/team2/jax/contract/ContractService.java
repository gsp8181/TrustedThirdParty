package com.team2.jax.contract;

import com.team2.jax.contract.input.StartSign;


public class ContractService {

	private static ContractValidator validator = new ContractValidator();
	
	public void start(StartSign ssObj) {
		validator.validate(ssObj);

		
		
	}

	//private static ContractRepository cod = new ContractRepositoryMemory();
	
	
	
}
