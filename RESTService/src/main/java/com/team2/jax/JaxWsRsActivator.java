package com.team2.jax;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Required class for using Java-EE type servers (TomEE/JBoss)
 */
@ApplicationPath("/rest") 
public class JaxWsRsActivator extends Application { }