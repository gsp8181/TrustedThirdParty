package com.team2.jax.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.team2.jax.certificates.Certificate;

public class ContractRepositoryMemory implements ContractRepository {

	private static List<Contract> repo = new ArrayList<Contract>();
	
	@Override
	public Contract create(Contract c) {
		c.setId(String.valueOf(repo.size()));
		c.setDocRef("http://www.google.co.uk/");
		repo.add(c);
		return c;
	}

	@Override
	public List<Contract> getUnsignedContractsByRecipient(String recipient) {
		List<Contract> results = new ArrayList<Contract>();
		for(Contract c : repo)
		{
			if(!c.isCompleted() && c.getRecipient().equals(recipient))
				results.add(c);
		}
		return results;
	}

	@Override
	public Contract getById(String id) {
		return repo.get(Integer.parseInt(id));
	}

}
