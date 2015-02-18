package com.team2.jax.certificates;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/certificates")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class CertificateRESTService {
	

    /**
     * Hello, World!
     * 
     * @return JSON wow
     */
    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {
    	throw new NotSupportedException();
        //response.setVal(msg);
        //return Response.ok(response).build();
    }
	
}
