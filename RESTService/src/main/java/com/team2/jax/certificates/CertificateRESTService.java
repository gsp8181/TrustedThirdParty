package com.team2.jax.certificates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
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
import javax.ws.rs.core.Context;
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

	private static CertificateService service = new CertificateService();

	@Context  //injected response proxy supporting multiple threads
	private HttpServletResponse response;
	
	/**
	 * <p>
	 * Retrieve a certificate by associated username.
	 * </p>
	 * <p>
	 * If the certificate has not been verified by email then it will not
	 * display
	 * </p>
	 * 
	 * @param email
	 *            The email of the desired certificate
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

	}

	/**
	 * <p>
	 * Save a certificate to the service.
	 * </p>
	 * <p>
	 * The certificate should have a valid email, the base64 encoded public key
	 * and a signed field which is the email signed using the key
	 * </p>
	 * <p>
	 * Errors will return in the form of {field:error}. If the email has not yet
	 * been validated then it prompt the user to verify their email before using
	 * the system
	 * </p>
	 * 
	 * @param cert
	 *            The certificate object to put into the database
	 * @return The certificate object added to the database
	 * @see Certificate
	 * @see CertificateIn
	 */
	@POST
	public Certificate sendCert(CertificateIn cert) {
		if (cert == null)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);

		try {
			Certificate out = service.create(cert);
			response.setStatus(Response.Status.CREATED.getStatusCode());
			return out;

		} catch (ConstraintViolationException ce) {
			// Handles bean specific constraint exceptions
			throw new WebApplicationException(
					createViolationResponse(ce.getConstraintViolations()));
		} catch (ValidationException ve) {
			// Handles CertificateValidatior thrown exception
			throw new WebApplicationException(
					createValidationViolationResponse(ve));
		}

	}

	/**
	 * <p>
	 * Verify a certificate.
	 * </p>
	 * <p>
	 * When a user creates a certificate, its verified status is initially set
	 * to false. The user will be prompted via email with a unique verification
	 * code which will trigger this method. If the verification code matches
	 * then the certificate can be used
	 * </p>
	 * <p>
	 * For example
	 * http://server/endpoint/rest/certificates/verify/test@email.com
	 * ?code=123bgaaf
	 * </p>
	 * 
	 * @param email
	 *            Email of the user to verify
	 * @param code
	 *            The unique generated code
	 * @return {"success":message} or {"error":message}
	 */
	@GET
	@Path("/verify")
	public Response verifyEmail(@QueryParam("email") String email,
			@QueryParam("code") String code) {
		boolean verified = service.verify(email, code);

		if (verified) {
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("success", "email verified");
			return Response.status(Response.Status.OK).entity(responseObj)
					.build();
		} else {
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj
					.put("error",
							"verification failed, are you sure you have the right email?");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(responseObj).build();
		}

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

}