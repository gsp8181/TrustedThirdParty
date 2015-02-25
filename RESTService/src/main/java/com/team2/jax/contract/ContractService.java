package com.team2.jax.contract;

import java.util.ArrayList;
import java.util.List;


public class ContractService {

	private static ContractValidator validator = new ContractValidator();
	
	private static ContractRepository cod = new ContractRepositoryMemory();
	
	public Contract start(ContractStart ssObj) throws Exception {
		validator.validate(ssObj);
		
		Contract c = new Contract();
		
		c.setIntermediateContract(ssObj.getSig());
		c.setSender(ssObj.getUsername());
		c.setRecipient(ssObj.getRecipient());
		
		// PUT QUEUE LOGIC HERE
		
		return cod.create(c);
		
		
	}

	public List<ContractIntermediate> getIntermediates(String recipient) {
		List<Contract> contracts = getUnsignedContractsByRecipient(recipient);
		
		List<ContractIntermediate> result = new ArrayList<ContractIntermediate>();
		for(Contract c : contracts)
		{
			ContractIntermediate i = new ContractIntermediate();
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

	public String counterSign(ContractComplete completeContract, String id) throws Exception {
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
