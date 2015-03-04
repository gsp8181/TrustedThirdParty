package com.team2.jax.contract;

import java.util.List;

public interface ContractRepository {

	public void create(Contract c);

	public List<Contract> getUnsignedContractsByRecipient(String recipient);

	public Contract getById(String id);
	
	public Contract deleteById(String id);
	
}
