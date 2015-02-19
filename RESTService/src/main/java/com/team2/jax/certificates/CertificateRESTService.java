package com.team2.jax.certificates;

import java.util.logging.Logger;

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
	
    //@Inject
    //private @Named("logger") Logger log;
    
    //@Inject
	//private CertificateService service; TODO: figure out injection
    private static CertificateService service = new CertificateService();

    
    @GET
    @Path("/{param}")
    public Response getCertByUsername(@PathParam("param") String username) {
    	Certificate cert = service.findByUsername(username);
    	if(cert == null)
    		throw new WebApplicationException(Response.Status.NOT_FOUND); //TODO: doesn't display an error message
    	
    	
        return Response.ok(cert).build();
    }
    
    @POST
    public Response sendCert(Certificate cert)
    {
    	
    	throw new RuntimeException();
    }
	
}
