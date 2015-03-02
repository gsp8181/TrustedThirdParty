package com.team2.jax.contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryDynamo;
import com.team2.security.CertificateTools;


public class ContractService {

	private CertificateRepository cs = new CertificateRepositoryDynamo();
	
	private static ContractValidator validator = new ContractValidator();
	
	private static ContractRepository cod = new ContractRepositoryDynamo();
	
	private static ContractFileStore cfs = new ContractFileStoreLocal();
	
	public ContractIntermediate start(ContractStart ssObj) throws Exception {
		validator.validate(ssObj);
		Contract c = new Contract();
		
		c.setDocName(ssObj.getDocName());
		
		byte[] doc = CertificateTools.decodeBase64(ssObj.getDocData());
		
		c.setDocRef(cfs.saveFile(ssObj.getDocName(), doc));
		c.setCompleted(false);
		c.setIntermediateContract(ssObj.getSig());
		c.setSender(ssObj.getEmail());
		c.setRecipient(ssObj.getRecipient());
		c.setSenderTime(cs.findByEmail(ssObj.getEmail()).getTime());
		c.setRecipientTime(cs.findByEmail(ssObj.getRecipient()).getTime());
		
		// SEND B AN EMAIL TELLING HIM HE HAS A DOCUMENT WAITING FROM A
		
		c = cod.create(c);
		
		ContractIntermediate i = new ContractIntermediate();
		i.setRecipient(c.getRecipient());
		i.setUsername(c.getSender());
		i.setDocName(c.getDocName());
		i.setSigSender(c.getIntermediateContract());
		i.setId(c.getId());
		
		return i;
		
		
	}

	public List<ContractIntermediate> getIntermediates(String recipient) {
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

	public String counterSign(ContractComplete completeContract, String id) throws Exception {
		Contract c = cod.getById(id);
		validator.validateComplete(completeContract, c);
		c.setContract(completeContract.getSig());
		c.setCompleted(true);
		cod.create(c);
		return c.getDocRef();
	}

	public String getDoc(String id, String signedId) throws Exception { //TODO: better handle
		Contract c = cod.getById(id);
		
		validator.validateDocRequest(id,signedId,c);
		
		return c.getDocRef();
	}

	public ContractComplete getContract(String id, String signedId) throws Exception {//TODO: better handle
		Contract c = cod.getById(id);
		
		validator.validateContractRequest(id,signedId,c);
		
		String contract = c.getContract();
		
		ContractComplete rObj = new ContractComplete(); 
		rObj.setSig(contract);
		
		return rObj;
	}

}
