package com.team2.jax.contract;

import java.util.ArrayList;
import java.util.List;

import com.team2.jax.contract.input.Complete;
import com.team2.jax.contract.input.Intermediate;
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
		
		// PUT QUEUE LOGIC HERE
		
		return cod.create(c);
		
		
	}

	public List<Intermediate> getIntermediates(String recipient) {
		List<Contract> contracts = getUnsignedContractsByRecipient(recipient);
		
		List<Intermediate> result = new ArrayList<Intermediate>();
		for(Contract c : contracts)
		{
			Intermediate i = new Intermediate();
			i.setRecipient(c.getRecipient());
			i.setUsername(c.getSender());
			i.setSigSender(c.getIntermediateContract());
			i.setId(c.getId());
			result.add(i);
		}
		
		if (result.isEmpty())
			return null;
		
		return result;
		
		
	}

	private List<Contract> getUnsignedContractsByRecipient(String recipient) {
		List<Contract> contracts = cod.getUnsignedContractsByRecipient(recipient);
		return contracts;
	}

	public String counterSign(Complete completeContract, String id) throws Exception {
		Contract c = cod.getById(id);
		validator.validateComplete(completeContract, c);
		c.setContract(completeContract.getSig());
		c.setCompleted(true);
		
		return c.getDocRef();
	}

	public String getDoc(String id, String signedId) throws Exception { //TODO: better handle
		Contract c = cod.getById(id);
		
		validator.validateDocRequest(id,signedId,c);
		
		return c.getDocRef();
	}

	public String getContract(String id, String signedId) throws Exception {//TODO: better handle
		Contract c = cod.getById(id);
		
		validator.validateContractRequest(id,signedId,c);
		
		return c.getContract();
	}

}
