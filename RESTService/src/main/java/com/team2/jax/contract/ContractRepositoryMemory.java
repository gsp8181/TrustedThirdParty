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
		repo.add(c);
		return c;
	}

}
