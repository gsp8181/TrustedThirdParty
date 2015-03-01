package com.team2.jax.contract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateService;
import com.team2.security.CertificateTools;

/**
 * <p>
 * Contract REST Service
 * </p>
 * <p>
 * Contains JAX-RS bindings for the operations that can be performed on a contract. 
 * </p>
 * 
 * @author Geoffrey Prytherch <gsp8181@users.noreply.github.com>
 * @see ContractService
 * @since 2015-02-23
 */
@Path("/contracts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class ContractRESTService {
	
	private static ContractService service = new ContractService();
	
	/**
	 * @param ssObj
	 * @return
	 */
	@POST
	@Path("/1")
	public Response startSign(ContractStart ssObj)
	{
		if (ssObj == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		Response.ResponseBuilder builder = null;
		
		try {
		ContractIntermediate out = service.start(ssObj);//TODO: hashmap?
		builder = Response.status(Response.Status.CREATED).entity(out);
		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			builder = createViolationResponse(ce.getConstraintViolations()); 
		} catch (ValidationException ve) {
			builder = createValidationViolationResponse(ve);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(
					responseObj);
		}
		
		return builder.build();
	}
	
	/**
	 * <p>
	 * 
	 * </p>
	 * @param username
	 * @return
	 */
	@GET
	@Path("/2/{username}")
	public List<ContractIntermediate> startCounterSign(@PathParam("username") String username)
	{
		List<ContractIntermediate> intermediates = service.getIntermediates(username); //TODO: hashmap?
		if (intermediates == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		
		return intermediates;
		//return Response.ok(intermediates).build();
	}
	
	/**
	 * <p>
	 * Step 3 - Counter Sign (and implied step 4).
	 * </p>
	 * <p>
	 * This method takes an ID of a contract (from step 4) and a ContractComplete object which is just really a fancy POJO that is {"sig":"base64sig"} and the sig parameter is the sigSender from the contract object signed with the users public key. It will return the docRef object (see step 4) aswell.
	 * </p>
	 * <p>
	 * Will give a BAD_REQUEST if the contract is completed or a {"field":"message"} exception if there is a slight problem and obviously a 404 if a contract cannot be found on the specified ID.
	 * </p>
	 * @param id The ID of the contract to be signed
	 * @param contract A {"sig":"base64sig"} object which is the signed signature from the ContractIntermediate object gotten from part 3
	 * @return The {"docRef":"URL"} object where the user can retrieve the document from
	 */
	@POST //TODO: PUT
	@Path("/3/{id}")
	public Response counterSign(@PathParam("id") String id, ContractComplete contract)
	{
		if (contract == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		Response.ResponseBuilder builder = null;
		
		try {
		//Contract out = service.counterSign(contract,id);
			String docRef = service.counterSign(contract,id);
			Map<String, String> out = new HashMap<String, String>();
			out.put("docRef",docRef);
			builder = Response.status(Response.Status.ACCEPTED).entity(out); //TODO:accepted?
		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException ve) {
			builder = createValidationViolationResponse(ve);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(
					responseObj);
		}
		
		return builder.build();
	}
	
	
	/**
	 * <p>
	 * Step 4 - Fetch Contract URL.
	 * </p>
	 * <p>
	 * Returns the URL to the contract that is valid for a set amount of time if the user has been successfully verified. This verification happens by the user signing the ID with their own private key.
	 * </p>
	 * <p>
	 * If the contract is not found a 404 will be returned, if the contract
	 * is not completed then a 403 forbidden will be returned. If the cert 
	 * cannot be found in the database then a 401 unauthorised will be returned 
	 * and a 401 unauthorised will be returned if signedId fails to verify
	 * with the sender.
	 * </p>
	 * @param id The id of the contract data to be returned
	 * @param signedId id signed with the key of the sender
	 * @return The JSON object containing the temp URL of the document for instance {"docRef":"http://s3.com/doc55.txt"}
	 * @throws Exception 
	 */
	@GET
	@Path("/4/{id}")
	public Response getDoc(@PathParam("id") String id, @QueryParam("signedId") String signedId) throws Exception//TODO: better handle
	{
		String docRef = service.getDoc(id, CertificateTools.base64urldecode(signedId)); //TODO: hashmap?
		if (docRef == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		Map<String, String> out = new HashMap<String, String>();
		out.put("docRef",docRef);
		
		
		return Response.status(Response.Status.OK).entity(out).build();
	}
	
	/**
	 * <p>
	 * Step 5 - Return Contract.
	 * </p>
	 * <p>
	 * Returns a correctly signed contract to the initial sender
	 * of the contract. The request will need to be correctly signed
	 * and verified to prove the identity of the sending user. The signed request is currently a signed version of the ID but my include timestamps in the future.
	 * </p>
	 * <p>
	 * If the contract is not found a 404 will be returned, if the contract
	 * is not completed then a 403 forbidden will be returned. If the cert 
	 * cannot be found in the database then a 401 unauthorised will be returned 
	 * and a 401 unauthorised will be returned if signedId fails to verify
	 * with the sender.
	 * </p>
	 * @param id The id of the contract to be returned
	 * @param signedId id signed with the key of the sender
	 * @return The contract in the form of SigB(SigA(H(doc))) as a JSON object for example {"sig":"abcdefg=="}
	 * @throws Exception
	 */
	@GET
	@Path("/5/{id}") //TODO: signedId should include a timestamp
	public ContractComplete getContract(@PathParam("id") String id, @QueryParam("signedId") String signedId) throws Exception //TODO: better handle TODO:change response to an actual type
	{
		ContractComplete out = service.getContract(id, CertificateTools.base64urldecode(signedId));
		if (out == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); // TODO: doesn't display an error message
		
		return out;
	}
	
	/**
	 * <p>
	 * Creates a JAX-RS "Bad Request" response including a map of all violation
	 * fields, and their message. This can be used by calling client
	 * applications to display violations to users.
	 * <p/>
	 * 
	 * @param violations
	 *            A Set of violations that need to be reported in the Response
	 *            body
	 * @return A Bad Request (400) Response containing all violation messages
	 */
	private Response.ResponseBuilder createViolationResponse(
			Set<ConstraintViolation<?>> violations) {

		Map<String, String> responseObj = new HashMap<String, String>();

		for (ConstraintViolation<?> violation : violations) {
			responseObj.put(violation.getPropertyPath().toString(),
					violation.getMessage());
		}

		return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
	}
	
	/**
	 * <p>
	 * Creates a JAX-RS "Bad Request" response including a map of all validation
	 * violation fields, and their message. This can be used by calling client
	 * applications to display violations to users. The validation violation
	 * message will typically take the form of "field:message"
	 * <p/>
	 * 
	 * @param violations
	 *            A Set of violations that need to be reported in the Response
	 *            body
	 * @return A Bad Request (400) Response containing all violation messages
	 */
	private Response.ResponseBuilder createValidationViolationResponse(
			ValidationException ve) {
		Response.ResponseBuilder builder;
		Map<String, String> responseObj = new HashMap<String, String>();
		String message = ve.getMessage();
		String field = message.substring(0,message.indexOf(':'));
		String error = message.substring(message.indexOf(':') + 1, message.length());
			responseObj.put(field, error);
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		return builder;
	}
	
}
