# RESTService

Install Amazon Eclipse Tools (Eclipse Marketplace) to build me!, you WILL need Eclipse for Java EE not just Java

##To set up Tomcat

Ensure you have the Amazon Plugins installed (type amazon into eclipse marketplace)

Go to the server management pane (Window->Show View->Server->Servers)

Right click->New->Server

Apache->Tomcat->Tomcat v7->Next

Download and Install

Then press Next and finish

Double click on the tomcat server in the server management pane

Check use tomcat installation and hit finish

In Tomcat installation directory, find conf/tomcat-users.xml

Add the following between the <tomcat-users>...</tomcat-users> tags

    <user username="tomcat" password="tomcat" roles="manager-script,manager-gui" />

##To import the project

    mvn clean package
    mvn tomcat7:deploy
