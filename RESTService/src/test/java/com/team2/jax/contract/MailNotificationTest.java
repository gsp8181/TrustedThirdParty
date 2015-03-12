package com.team2.jax.contract;

import com.team2.jax.ses.EmailNotifier;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for email notification using AWS SES
 *
 */

public class MailNotificationTest {
	
	private static final String senderEmail = "savmukherjee@gmail.com";
	private static final String receiverEmail = "beingdin@gmail.com";
	private static EmailNotifier notifier = EmailNotifier.getInstance();
	private static final String contractId = "1234567890";
	

	@Test
	// Tests that an Email with a verification link is sent to the users. 
	 public void testVerifyEmailIdentity() throws Exception{		
		//assertNotNull(notifier.verifyEmailIdentity(senderEmail));
		//assertNotNull(notifier.verifyEmailIdentity(receiverEmail));		
	}

	@Test
	//Tests that a user has an already verified email
	 public void testVerifiedEmailIdentity() throws Exception{		
		//assertEquals(notifier.hasVerifiedEmail(senderEmail),true);
		//assertEquals(notifier.hasVerifiedEmail(receiverEmail),true);		
	}
	
	@Test
	//Tests email notifications
	 public void testSendEmail() throws Exception{
		//assertNotNull(notifier.sendEmail(senderEmail, receiverEmail, EmailNotifier.LINK_CONTEXT,contractId).getMessageId());
		//assertNotNull(notifier.sendEmail(senderEmail, receiverEmail, EmailNotifier.COUNTERSIGN_CONTEXT,contractId).getMessageId());
		//assertNotNull(notifier.sendEmail(senderEmail, receiverEmail, EmailNotifier.GETDOC_CONTEXT, contractId).getMessageId());
		assertNotNull(notifier.sendEmail(senderEmail,receiverEmail, EmailNotifier.GETCONTRACT_CONTEXT,contractId).getMessageId());	
	}
	
	
	
}
