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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.team2.jax.certificates.Certificate;
import com.team2.jax.certificates.CertificateService;
import com.team2.jax.contract.input.Intermediate;
import com.team2.jax.contract.input.StartSign;

@Path("/contracts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class ContractRESTService {
	
	private static ContractService service = new ContractService();
	
	@POST
	@Path("/1")
	public Response startSign(StartSign ssObj)
	{
		if (ssObj == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		Response.ResponseBuilder builder = null;
		
		try {
		Contract out = service.start(ssObj);
		builder = Response.status(Response.Status.CREATED).entity(out);
		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException ve) {
			Map<String, String> responseObj = new HashMap<String, String>();
			if (ve.getMessage().startsWith("No certificate was found for the designated sender")) {
				responseObj.put("username", "No certificate was found for the designated sender"); //TODO: ve.getMessage()
				builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
			}	
			if(ve.getMessage().startsWith("Validation of the signature failed, make sure the signing key is the database")) {
				responseObj.put("sig", "Validation of the signature failed, make sure the signing key is the database"); //TODO: ve.getMessage() TODO:correct sig not is in database
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
	public List<Intermediate> startCounterSign(@PathParam("username") String username)
	{
		List<Intermediate> intermediates = service.getIntermediates(username);
		if (intermediates == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); // TODO: doesn't display an error message

		
		return intermediates;
		//return Response.ok(intermediates).build();
	}
	
	@POST
	@Path("/3")
	public Response counterSign()
	{
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
	}
	
	
	@GET
	@Path("/4")
	public Response getDoc()
	{
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
	}
	
	@GET
	@Path("/5")
	public Response getContract()
	{
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
