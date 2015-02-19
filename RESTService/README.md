# RESTService

Install Amazon Eclipse Tools (Eclipse Marketplace) to build me!, you WILL need Eclipse for Java EE not just Java

##To set up Tomcat

* Ensure you have the Amazon Plugins installed (type amazon into eclipse marketplace)

* Go to the server management pane (Window->Show View->Server->Servers)

* Right click->New->Server

* Apache->Tomcat->Tomcat v7->Next

* Download and Install

* Then press Next and finish

* Double click on the tomcat server in the server management pane

* Check use tomcat installation and hit finish

* In Tomcat installation directory, find conf/tomcat-users.xml

* Add the following between the `<tomcat-users>...</tomcat-users>` tags

`<user username="tomcat" password="tomcat" roles="manager-script,manager-gui" />`
    
* Start tomcat in eclipse

* Verify that tomcat is working by going to [http://localhost:8080/](http://localhost:8080/)

##To import the project

Add to eclipse as a new maven project

##Maven Commands

    mvn clean package
    mvn tomcat7:deploy
    mvn tomcat7:undeploy 
    mvn tomcat7:redeploy

##Test Links

[http://localhost:8080/service/rest/hellorest/yourmessage](http://localhost:8080/service/rest/hellorest/yourmessage)
