package com.team2.jax.certificates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * <p>
 * Certificate REST Service
 * </p>
 * <p>
 * Contains JAX-RS bindings for adding certificates to the repository and
 * viewing the public key associated with a user
 * </p>
 * 
 * @author Geoffrey Prytherch <gsp8181@users.noreply.github.com>
 * @since 2015-02-18
 * @see CertificateService
 */
@Path("/certificates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class CertificateRESTService {

	// @Inject
	// private @Named("logger") Logger log;

	// @Inject
	// private CertificateService service; TODO: figure out injection
	private static CertificateService service = new CertificateService();

	/**
	 * <p>
	 * Retrieve a certificate by associated username
	 * </p>
	 * 
	 * @param email The email of the desired certificate
	 * @return The certificate object
	 * @see Certificate
	 */
	@GET
	@Path("/{email}")
	public Certificate getCertByUsername(@PathParam("email") String email) {
		Certificate cert = service.findByEmail(email);
		if (cert == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		return cert;
		
		//return Response.ok(cert).build();
	}

	/**
	 * @param cert
	 * @return
	 * @see Certificate
	 * @see CertificateIn
	 */
	@POST
	public Response sendCert(CertificateIn cert) {

		if (cert == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		Response.ResponseBuilder builder = null;

		try {
			Certificate out = service.create(cert);

			builder = Response.status(Response.Status.CREATED).entity(out);

		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException ve) {
			// Handles CertificateValidatior thrown exception
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
