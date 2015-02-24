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
	 * Get a certificate by username
	 * @param username The username of the desired certificate
	 * @return The certificate object
	 */
	@GET
	@Path("/{username}")
	public Certificate /*Response*/ getCertByUsername(@PathParam("username") String username) {
		Certificate cert = service.findByUsername(username);
		if (cert == null)
			throw new WebApplicationException(Response.Status.NOT_FOUND); // TODO: doesn't display an error message

		return cert;
		//return Response.ok(cert).build();
	}

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
			Map<String, String> responseObj = new HashMap<String, String>();
			if (ve.getMessage().startsWith("Username Already Exists")) {
				responseObj.put("username", "Username Already Exists");
				builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
			}
			if (ve.getMessage().startsWith("Certificate verification failed")) {
				responseObj.put("publicKey", "Certificate verification failed");
				builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
			}
			//builder = Response.status(Response.Status.CONFLICT).entity(responseObj);

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
