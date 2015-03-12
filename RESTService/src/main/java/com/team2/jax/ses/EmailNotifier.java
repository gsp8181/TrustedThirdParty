package com.team2.jax.ses;

import java.util.HashMap;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;

public class EmailNotifier {
	
	private final static AWSCredentialsProviderChain credentials;
	private final static AmazonSimpleEmailServiceClient sesClient;
	
	private static HashMap<String,String> NOTIFICATION_CONTEXT= new HashMap<String,String>();
	
	/* Set Notification contexts to be used for a contract signing*/
	public final static String COUNTERSIGN_CONTEXT = "NOTIFICATION OF ORIGIN";
	public final static String GETDOC_CONTEXT  = "DOCUMENT NOTIFICATION";
	public final static String GETCONTRACT_CONTEXT   = "NOTIFICATION OF RECEIPT";
	public final static String LINK_CONTEXT   = "VERIFICATION NOTIFICATION"; 
	
	private final static String VERIFICATION_STATUS_SUCCESS   = "Success";
	//private final static String VERIFICATION_STATUS_PENDING   = "Pending";
	
	private final static String TDS_EMAIL  = "tds.noreply@gsp8181.co.uk";
	private final static String SUBJECT = "AWS TDS : ";
	private final static String SIGNATURE = String.format("%n%n") + "REGARDS"+ String.format("%n") +"TDS TEAM" + String.format("%n") + "https://ttp.gsp8181.co.uk/";
	
	/* Set SES Credentials*/
	static{
	  	credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
		sesClient = new AmazonSimpleEmailServiceClient(credentials);
		sesClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
		
		NOTIFICATION_CONTEXT.put(COUNTERSIGN_CONTEXT,"A contract is waiting for you using the TDS service. Please view the details using the TDS interface and sign if you are happy to accept. Stored at ID ");
		NOTIFICATION_CONTEXT.put(GETDOC_CONTEXT,"The counter signature was verified and you are now authorised to retrieve contract ");
		NOTIFICATION_CONTEXT.put(GETCONTRACT_CONTEXT,"The contract was successfully countersigned by the recipient, you may now retrieve the completed contract from the registry stored at ID ");
		NOTIFICATION_CONTEXT.put(LINK_CONTEXT,"You have recently signed up for the TDS service (or added a new private key) at https://ttp.gsp8181.co.uk/ If you did not take this action, please ignore this email. If you wish to accept the change, please follow this link to verify your private key: https://ttp.gsp8181.co.uk/rest/certificates/verify?");
	}
	
	
	private EmailNotifier() {
		//Prevent instantiation
	}
	
	 public static EmailNotifier getInstance() {
		 return new EmailNotifier();
	 }
	
	/** Verify Email address of the user trying to obtain a certificate from TDS.
	 *  An Email with a verification link is sent to the user. 
	 *  The user is authorized to receive further emails from TDS only after he has confirmed using this verification link. 
	 * 
	 * @param email - Email ID of the User trying to obtain a certificate from TDS
	 * @throws Exception
	 */
	public VerifyEmailIdentityResult verifyEmailIdentity(String email) throws Exception{
		VerifyEmailIdentityResult result = sesClient.verifyEmailIdentity(new VerifyEmailIdentityRequest().withEmailAddress(email));
		
		return result;
	}
	
	
	/** Check if the user trying to obtain a contract from TDS, has a verified email.
	 *  A verification email was sent to the user when he previously obtained a certificate from TDS.
	 *  It is assumed for fair exchange, that the user has already verified his email at this point.
	 *  If not, such a user fails validation for obtaining the contract. 
	 * 
	 * @param email - Email ID of the User trying to obtain a contract from TDS
	 * @throws Exception
	 */	
	public boolean hasVerifiedEmail(String email) throws Exception{
		boolean verificationComplete = false;
		GetIdentityVerificationAttributesResult verificationAttributes = sesClient.getIdentityVerificationAttributes(new GetIdentityVerificationAttributesRequest().withIdentities(email));
		if(verificationAttributes.getVerificationAttributes().get(email)!=null){
			String verificationStatus = verificationAttributes.getVerificationAttributes().get(email).getVerificationStatus();
			if(verificationStatus !=null && verificationStatus.equalsIgnoreCase(VERIFICATION_STATUS_SUCCESS))
				verificationComplete = true;
		}		
		return verificationComplete;
	}
	
	/** Send an email to a previously verified email id.
	 * 
	 * @param senderEmail - The verified email id of the contract sender
	 * @param receipientMail - The verified email id of the contract receiver
	 * @param notificationContext - The context that identifies the state of contract signing in TDS
	 * @throws Exception
	 */
	
	public SendEmailResult sendEmail(String senderEmail, String receipientMail, String notificationContext, String contractId) throws Exception{	
        Destination destination = new Destination().withToAddresses(new String[]{receipientMail});    
        Content subject = new Content().withData(SUBJECT + notificationContext);
        
        Content textBody=null;
        if(notificationContext.equalsIgnoreCase(LINK_CONTEXT))
        	textBody= new Content().withData(NOTIFICATION_CONTEXT.get(LINK_CONTEXT)+"email="+ receipientMail + "&code=" + contractId + SIGNATURE);
        else        	
        	textBody = new Content().withData(NOTIFICATION_CONTEXT.get(notificationContext) + contractId + SIGNATURE); 
        
        
        Body body = new Body().withText(textBody);         
        Message message = new Message().withSubject(subject).withBody(body);    
        SendEmailRequest request = new SendEmailRequest().withSource(TDS_EMAIL).withDestination(destination).withMessage(message);
        SendEmailResult mailResult = sesClient.sendEmail(request);
        
        return mailResult; 
    }
	
		
}
