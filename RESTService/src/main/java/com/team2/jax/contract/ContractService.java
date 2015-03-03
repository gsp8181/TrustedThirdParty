package com.team2.jax.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryDynamo;
import com.team2.security.CertificateTools;


public class ContractService {

	private CertificateRepository cs = new CertificateRepositoryDynamo();
	
	private static ContractValidator validator = new ContractValidator();
	
	private static ContractRepository cod = new ContractRepositoryDynamo();
	
	private static ContractFileStore cfs = new ContractFileStoreS3();
	
	public ContractIntermediate start(ContractStart ssObj) {
		validator.validate(ssObj);
		Contract c = new Contract();
		
		c.setDocName(UUID.randomUUID().toString() + "." + ssObj.getDocName());
		
		byte[] doc = CertificateTools.decodeBase64(ssObj.getDocData());
		
		try {
		c.setDocRef(cfs.saveFile(c.getDocName(), doc));
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		c.setCompleted(false);
		c.setIntermediateContract(ssObj.getSig());
		c.setSender(ssObj.getEmail());
		c.setRecipient(ssObj.getRecipient());
		c.setSenderTime(cs.findByEmail(ssObj.getEmail()).getTime());
		c.setRecipientTime(cs.findByEmail(ssObj.getRecipient()).getTime());
		
		EmailNotifier emailNotifier = EmailNotifier.getInstance();
		try{
		emailNotifier.sendEmail(ssObj.getEmail(), ssObj.getRecipient(), EmailNotifier.COUNTERSIGN_CONTEXT, c.getId());
	} catch (Exception e) {
		throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
	}
		
		c = cod.create(c);
		
		ContractIntermediate i = new ContractIntermediate();
		i.setRecipient(c.getRecipient());
		i.setUsername(c.getSender());
		i.setDocName(c.getDocName());
		i.setSigSender(c.getIntermediateContract());
		i.setId(c.getId());
		
		return i;
		
		
	}

	public List<ContractIntermediate> getIntermediates(String recipient, long ts, String signedStamp) {
		validator.validateIntRequest(recipient,ts,signedStamp);
		
		List<Contract> contracts = getUnsignedContractsByRecipient(recipient);
		
		List<ContractIntermediate> result = new ArrayList<ContractIntermediate>();
		for(Contract c : contracts)
		{
			ContractIntermediate i = new ContractIntermediate();
			i.setRecipient(c.getRecipient());
			i.setUsername(c.getSender());
			i.setDocName(c.getDocName());
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

	public ContractDoc counterSign(ContractComplete completeContract, String id) {
		Contract c = cod.getById(id);
			validator.validateComplete(completeContract, c);
		c.setContract(completeContract.getSig());
		c.setCompleted(true);
		
		EmailNotifier emailNotifier = EmailNotifier.getInstance();
		try {
			//emailNotifier.sendEmail(c.getSender(), c.getRecipient(), EmailNotifier.GETDOC_CONTEXT, c.getId());
			emailNotifier.sendEmail(c.getRecipient(), c.getSender(), EmailNotifier.GETCONTRACT_CONTEXT, c.getId());
		} catch (Exception e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
		
		cod.create(c);
		ContractDoc out = new ContractDoc();
		
		out.setDocRef(cfs.getTempLink(c.getDocRef()));
		
		return out;
	}

	public ContractDoc getDoc(String id, long ts, String signedId) {
		Contract c = cod.getById(id);
		
			validator.validateDocRequest(id,ts,signedId,c);
		
		ContractDoc out = new ContractDoc();
		
		out.setDocRef(cfs.getTempLink(c.getDocRef()));
		
		return out;
	}

	public ContractComplete getContract(String id, long ts, String signedId) {
		Contract c = cod.getById(id);
		
		validator.validateContractRequest(id,ts,signedId,c);
		
		String contract = c.getContract();
		
		ContractComplete rObj = new ContractComplete(); 
		rObj.setSig(contract);
		
		return rObj;
	}

}
