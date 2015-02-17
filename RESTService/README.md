# RESTService

Install Amazon Eclipse Tools (Eclipse Marketplace) to build me!, you WILL need Eclipse for Java EE not just Java

##To set up Tomcat

Ensure you have the Amazon Plugins installed (type amazon into eclipse marketplace)

Go to the server management pane (Window->Show View->Server->Servers)

Right click->New->Server

Apache->Tomcat->Tomcat v7->Next

Download and Install

Then press Next and finish

##To import the project

Make a new Dynamic Web Service project for Tomcat (Not Amazon for now)

Close that project

Clone the RESTService directory into the project you just created

Open the project again

Right click on the Tomcat server in servers, press add and remove and move the service from available to configured

Finish, right click and press start

Visit http://localhost:8080/RESTService

You can right click the tomcat service and press publish to update changes
