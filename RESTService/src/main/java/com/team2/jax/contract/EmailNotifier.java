package com.team2.jax.contract;

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
	public final static String COUNTERSIGN_CONTEXT = "INTERMEDIATE NOTIFICATION";
	public final static String GETDOC_CONTEXT  = "GET DOCUMENT NOTIFICATION";
	public final static String GETCONTRACT_CONTEXT   = "GET COMPLETED CONTRACT NOTIFICATION";
	public final static String LINK_CONTEXT   = "GET COMPLETED CONTRACT NOTIFICATION"; //TODO: <-
	
	private final static String VERIFICATION_STATUS_SUCCESS   = "Success";
	//private final static String VERIFICATION_STATUS_PENDING   = "Pending";
	
	private final static String TDS_EMAIL  = "g2awsses@gmail.com";
	private final static String SUBJECT = "TDS Notification : Non-Repudiation Receipt from ";
	
	/* Set SES Credentials*/
	static{
	  	credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain());
		sesClient = new AmazonSimpleEmailServiceClient(credentials);
		sesClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
		
		NOTIFICATION_CONTEXT.put(COUNTERSIGN_CONTEXT,"YOUR CONTRACTED DOUCMENT HAS BEEN RECEIVED BY TDS. PLEASE COUNTER SIGN THE DOCUMENT USING CONTRACT ID ");
		NOTIFICATION_CONTEXT.put(GETDOC_CONTEXT,"COUNER SIGNATURE VERIFIED BY TDS. YOU ARE NOW AUTHORISED TO RETRIEVE THE DOCUMENT FROM TDS USING CONTRACT ID ");
		NOTIFICATION_CONTEXT.put(GETCONTRACT_CONTEXT,"CONTRACT COMPLETED BY TDS. YOU ARE NOW AUTHORISED TO RETRIEVE THE FINAL CONTRACT FROM TDS USING CONTRACT ID ");
		NOTIFICATION_CONTEXT.put(LINK_CONTEXT,"http://ttp.gsp8181.co.uk/rest/certificates/verify?email=newCert.getEmail()&code=newCert.getCode()"); //TODO <-
		
		
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
        Content subject = new Content().withData(SUBJECT + senderEmail);
        Content textBody = new Content().withData(NOTIFICATION_CONTEXT.get(notificationContext) + contractId); 
        Body body = new Body().withText(textBody);      
        Message message = new Message().withSubject(subject).withBody(body);    
        SendEmailRequest request = new SendEmailRequest().withSource(TDS_EMAIL).withDestination(destination).withMessage(message);
        SendEmailResult mailResult = sesClient.sendEmail(request);
        
        return mailResult; 
    }
	
}
