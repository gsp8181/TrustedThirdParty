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

@Path("/contracts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class ContractRESTService {
	
	private static ContractService service = new ContractService();
	
	@POST
	@Path("/1")
	public Response startSign(ContractStart ssObj)
	{
		if (ssObj == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		Response.ResponseBuilder builder = null;
		
		try {
		Contract out = service.start(ssObj);//TODO: hashmap?
		builder = Response.status(Response.Status.CREATED).entity(out);
		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			builder = createViolationResponse(ce.getConstraintViolations()); 
		} catch (ValidationException ve) {
			Map<String, String> responseObj = new HashMap<String, String>();
			if (ve.getMessage().startsWith("No certificate was found for the designated sender")) {
				responseObj.put("username", "No certificate was found for the designated sender");
				builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
			}	
			if(ve.getMessage().startsWith("Validation of the signature failed, make sure the signing key is the database")) {
				responseObj.put("sig", "Validation of the signature failed, make sure the signing key is the database");// TODO:correct sig not is in database
				builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
			}
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(
					responseObj);
		}
		
		return builder.build();
	}
	
	@GET
	@Path("/2/{username}")
	public List<ContractIntermediate> startCounterSign(@PathParam("username") String username) //TODO: USE INTERMEDIATE?
	{
		List<ContractIntermediate> intermediates = service.getIntermediates(username); //TODO: hashmap?
		if (intermediates == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); // TODO: doesn't display an error message

		
		return intermediates;
		//return Response.ok(intermediates).build();
	}
	
	@POST //TODO: PUT
	@Path("/3/{id}")
	public Response counterSign(@PathParam("id") String id, ContractComplete contract) //TODO: id -> int
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
			Map<String, String> responseObj = new HashMap<String, String>(); //TODO:make all like this
			String message = ve.getMessage();
			String field = message.substring(0,message.indexOf(':'));
			String error = message.substring(message.indexOf(':') + 1, message.length());
				responseObj.put(field, error);
				builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(
					responseObj);
		}
		
		return builder.build();
	}
	
	
	@GET
	@Path("/4/{id : \\d+}")
	public Response getDoc(@PathParam("id") String id, @QueryParam("signedId") String signedId) throws Exception//TODO: better handle
	{
		String docRef = service.getDoc(id, CertificateTools.base64urldecode(signedId)); //TODO: hashmap?
		if (docRef == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); // TODO: doesn't display an error message

		Map<String, String> out = new HashMap<String, String>();
		out.put("docRef",docRef);
		
		
		return Response.status(Response.Status.OK).entity(out).build();
	}
	
	@GET
	@Path("/5/{id : \\d+}")
	public Response getContract(@PathParam("id") String id, @QueryParam("signedId") String signedId) throws Exception //TODO: better handle
	{
		String docRef = service.getContract(id, CertificateTools.base64urldecode(signedId)); //TODO: hashmap?
		if (docRef == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); // TODO: doesn't display an error message

		Map<String, String> out = new HashMap<String, String>();
		out.put("contract",docRef);
		
		return Response.status(Response.Status.OK).entity(out).build();
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
	
}
