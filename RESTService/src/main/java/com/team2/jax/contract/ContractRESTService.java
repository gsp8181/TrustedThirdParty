package com.team2.jax.contract;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.team2.jax.contract.input.StartSign;

@Path("/contracts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class ContractRESTService {
	
	@POST
	@Path("/1")
	public Response startSign(StartSign ssObj)
	{
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
	}
	
	@GET
	@Path("/2")
	public Response startCounterSign()
	{
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
	
}
