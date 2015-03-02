package com.team2.jax.contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.team2.jax.certificates.CertificateRepository;
import com.team2.jax.certificates.CertificateRepositoryDynamo;
import com.team2.security.CertificateTools;


public class ContractService {

	private CertificateRepository cs = new CertificateRepositoryDynamo();
	
	private static ContractValidator validator = new ContractValidator();
	
	private static ContractRepository cod = new ContractRepositoryDynamo();
	
	private static ContractFileStoreS3 cfs = new ContractFileStoreS3();
	
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

	public ContractDoc counterSign(ContractComplete completeContract, String id) {
		Contract c = cod.getById(id);
		try {
			validator.validateComplete(completeContract, c);
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		c.setContract(completeContract.getSig());
		c.setCompleted(true);
		cod.create(c);
		ContractDoc out = new ContractDoc();
		
		out.setDocRef(cfs.getTempLink(c.getDocRef()));
		
		return out;
	}

	public ContractDoc getDoc(String id, String signedId) {
		Contract c = cod.getById(id);
		
		try {
			validator.validateDocRequest(id,signedId,c);
		} catch (Exception e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		ContractDoc out = new ContractDoc();
		
		out.setDocRef(cfs.getTempLink(c.getDocRef()));
		
		return out;
	}

	public ContractComplete getContract(String id, String signedId) {
		Contract c = cod.getById(id);
		
		validator.validateContractRequest(id,signedId,c);
		
		String contract = c.getContract();
		
		ContractComplete rObj = new ContractComplete(); 
		rObj.setSig(contract);
		
		return rObj;
	}

}
