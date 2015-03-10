package com.team2.jax.contract;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.team2.jax.contract.objects.ContractComplete;
import com.team2.jax.contract.objects.ContractDoc;
import com.team2.jax.contract.objects.ContractIntermediate;
import com.team2.jax.contract.objects.ContractStart;

/**
 * <p>
 * Contract REST Service
 * </p>
 * <p>
 * Contains JAX-RS bindings for the operations that can be performed on a
 * contract.
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
	 * <p>
	 * Step 1 - Start Signing.
	 * </p>
	 * <p>
	 * Will take an entity of the document (in base64 format) and the signed
	 * reference and add it to the database. Also verified if both signatures
	 * are in the database and verified otherwise an error will be thrown
	 * telling the user to either verify their own email or tell the remote user
	 * to verify theirs
	 * 
	 * @param ssObj
	 *            The object containing the contract information
	 * @return The dispatched contract
	 */
	@POST
	@Path("/1")
	public ContractIntermediate startSign(ContractStart ssObj) {
		if (ssObj == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		try {
			return service.start(ssObj);
		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			throw new WebApplicationException(
					createViolationResponse(ce.getConstraintViolations()));
		} catch (ValidationException ve) {
			throw new WebApplicationException(
					createValidationViolationResponse(ve));
		}
	}
	
	/**
	 * <p>
	 * Abort request.
	 * </p>
	 * <p>
	 * The sender can optionally abort the contract signing process which will remove all record from the database, this may only happen if the contract has not been countersigned (or an error will be thrown).
	 * </p> 
	 * <p>
	 * The signed request is the timestamp signed with the key of the initial
	 * reciever (sender of this request) and must be less than 5 minutes old.
	 * The signed data must be in base64 format and in URL format
	 * </p>
	 * @param id The ID of the contract to be deleted
	 * @param signedStamp
	 *            timestamp signed with the key of the sender (the initial
	 *            receiver of the contract) in Base64URL format
	 *            (CertificateTools.base64urlencode)
	 * @param ts
	 *            The UNIX epoch timestamp in seconds, MUST be less than 5
	 *            minutes old
	 * @return 204 (NO CONTENT) if deletion is successful or an error code if not
	 */
	@DELETE
	@Path("/abort/{id}")
	public Response abortContract(@PathParam("id") String id,
			@QueryParam("ts") long ts,
			@QueryParam("signedStamp") String signedStamp)
			{
		
			service.abort(id, ts, signedStamp);
		
			return Response.status(Response.Status.NO_CONTENT).build();
		
		
		
			}
			

	/**
	 * <p>
	 * Step 2 - Get available contracts to sign.
	 * </p>
	 * <p>
	 * The user will provide their details and will have returned a list of
	 * contracts they can sign. If there is none (or the user does not exist)
	 * then a 404 will be returned
	 * </p>
	 * <p>
	 * The signed request is the timestamp signed with the key of the initial
	 * reciever (sender of this request) and must be less than 5 minutes old.
	 * The signed data must be in base64 format and in URL format
	 * </p>
	 * 
	 * @param email
	 *            The email of the receiver.
	 * @param signedStamp
	 *            timestamp signed with the key of the sender (the initial
	 *            receiver of the contract) in Base64URL format
	 *            (CertificateTools.base64urlencode)
	 * @param ts
	 *            The UNIX epoch timestamp in seconds, MUST be less than 5
	 *            minutes old
	 * @return A list of contracts that can be signed by the user
	 */
	@GET
	@Path("/2/{email}")
	public List<ContractIntermediate> startCounterSign(
			@PathParam("email") String email, @QueryParam("ts") long ts,
			@QueryParam("signedStamp") String signedStamp) {
		List<ContractIntermediate> intermediates = service.getIntermediates(
				email, ts, signedStamp);
		if (intermediates == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		return intermediates;
	}

	/**
	 * <p>
	 * Step 3 - Counter Sign (and implied step 4).
	 * </p>
	 * <p>
	 * This method takes an ID of a contract (from step 4) and a
	 * ContractComplete object which is just really a fancy POJO that is
	 * {"sig":"base64sig"} and the sig parameter is the sigSender from the
	 * contract object signed with the users public key. It will return the
	 * docRef object (see step 4) aswell.
	 * </p>
	 * <p>
	 * Will give a BAD_REQUEST if the contract is completed or a
	 * {"field":"message"} exception if there is a slight problem and obviously
	 * a 404 if a contract cannot be found on the specified ID.
	 * </p>
	 * 
	 * @param id
	 *            The ID of the contract to be signed
	 * @param contract
	 *            A {"sig":"base64sig"} object which is the signed signature
	 *            from the ContractIntermediate object gotten from part 3
	 * @return The {"docRef":"URL"} object where the user can retrieve the
	 *         document from
	 */
	@POST
	@Path("/3/{id}")
	public ContractDoc counterSign(@PathParam("id") String id,
			ContractComplete contract) {
		if (contract == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		try {
			return service.counterSign(contract, id);
		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			throw new WebApplicationException(
					createViolationResponse(ce.getConstraintViolations()));
		} catch (ValidationException ve) {
			throw new WebApplicationException(
					createValidationViolationResponse(ve));
		}

	}

	/**
	 * <p>
	 * Step 4 - Fetch Contract URL.
	 * </p>
	 * <p>
	 * Returns the URL to the contract that is valid for a set amount of time if
	 * the user has been successfully verified. The signed request is the
	 * timestamp signed with the key of the initial reciever (sender of this
	 * request) and must be less than 5 minutes old. The signed data must be in
	 * base64 format and in URL format
	 * </p>
	 * <p>
	 * If the contract is not found a 404 will be returned, if the contract is
	 * not completed then a 403 forbidden will be returned. If the cert cannot
	 * be found in the database then a 401 unauthorised will be returned and a
	 * 401 unauthorised will be returned if signedId fails to verify with the
	 * sender.
	 * </p>
	 * 
	 * @param id
	 *            The id of the contract data to be returned
	 * @param signedStamp
	 *            timestamp signed with the key of the sender (the initial
	 *            receiver of the contract) in Base64URL format
	 *            (CertificateTools.base64urlencode)
	 * @param ts
	 *            The UNIX epoch timestamp in seconds, MUST be less than 5
	 *            minutes old
	 * @return The JSON object containing the temp URL of the document for
	 *         instance {"docRef":"http://s3.com/doc55.txt"}
	 * @see com.team2.jax.security.CertificateTools#genTimestamp(PrivateKey key)
	 */
	@GET
	@Path("/4/{id}")
	public ContractDoc getDoc(@PathParam("id") String id,
			@QueryParam("ts") long ts,
			@QueryParam("signedStamp") String signedStamp) {
		return service.getDoc(id, ts, signedStamp);
	}

	/**
	 * <p>
	 * Step 5 - Return Contract.
	 * </p>
	 * <p>
	 * Returns a correctly signed contract to the initial sender of the
	 * contract. The request will need to be correctly signed and verified to
	 * prove the identity of the sending user. The signed request is the
	 * timestamp signed with the key of the initial sender and must be less than
	 * 5 minutes old. The signed data must be in base64 format and in URL
	 * format.
	 * </p>
	 * <p>
	 * If the contract is not found a 404 will be returned, if the contract is
	 * not completed then a 403 forbidden will be returned. If the cert cannot
	 * be found in the database then a 401 unauthorised will be returned and a
	 * 401 unauthorised will be returned if signedId fails to verify with the
	 * sender.
	 * </p>
	 * 
	 * @param id
	 *            The id of the contract to be returned
	 * @param signedStamp
	 *            timestamp signed with the key of the sender (the initial
	 *            sender of the contract) in Base64URL format
	 *            (CertificateTools.base64urlencode)
	 * @param ts
	 *            The UNIX epoch timestamp in seconds, MUST be less than 5
	 *            minutes old
	 * @return The contract in the form of SigB(SigA(H(doc))) as a JSON object
	 *         for example {"sig":"abcdefg=="}
	 * @see com.team2.jax.security.CertificateTools#genTimestamp(PrivateKey key)
	 */
	@GET
	@Path("/5/{id}")
	public ContractComplete getContract(@PathParam("id") String id,
			@QueryParam("ts") long ts,
			@QueryParam("signedStamp") String signedStamp) {
		return service.getContract(id, ts, signedStamp);
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
	private Response createViolationResponse(
			Set<ConstraintViolation<?>> violations) {

		Map<String, String> responseObj = new HashMap<String, String>();

		for (ConstraintViolation<?> violation : violations) {
			responseObj.put(violation.getPropertyPath().toString(),
					violation.getMessage());
		}

		return Response.status(Response.Status.BAD_REQUEST).entity(responseObj)
				.build();
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
	private Response createValidationViolationResponse(ValidationException ve) {
		Response.ResponseBuilder builder;
		Map<String, String> responseObj = new HashMap<String, String>();
		String message = ve.getMessage();
		String field = message.substring(0, message.indexOf(':'));
		String error = message.substring(message.indexOf(':') + 1,
				message.length());
		responseObj.put(field, error);
		builder = Response.status(Response.Status.BAD_REQUEST).entity(
				responseObj);
		return builder.build();
	}

}
