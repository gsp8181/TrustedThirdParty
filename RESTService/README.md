# RESTService

Install Amazon Eclipse Tools (Eclipse Marketplace) to build me!, you WILL need Eclipse for Java EE not just Java

You will also NEED JDK8 and have it set up in the JAVA_HOME environment variable and have maven installed

##To set up TomEE (Tomcat)

* Download and unzip TomEE somewhere [http://www.apache.org/dyn/closer.cgi/tomee/tomee-1.7.1/apache-tomee-1.7.1-plus.zip](http://www.apache.org/dyn/closer.cgi/tomee/tomee-1.7.1/apache-tomee-1.7.1-plus.zip)

* In Tomcat installation directory, find conf/tomcat-users.xml

* Add the following between the `<tomcat-users>...</tomcat-users>` tags

`<user username="tomcat" password="tomcat" roles="manager-script,manager-gui" />`

* Go to the server management pane (Window->Show View->Server->Servers)

* Right click->New->Server

* Apache->Tomcat->Tomcat v7->Next

* Specify the location of the installation of TomEE

* Then press Next and finish

* Double click on the tomcat server in the server management pane

* Check use tomcat installation and hit finish
    
* Start tomcat in eclipse

* Verify that tomcat is working by going to [http://localhost:8080/](http://localhost:8080/)

##To import the project

Add to eclipse as a new maven project

##Maven Commands

    mvn clean package
    mvn clean tomcat7:deploy
    mvn clean tomcat7:undeploy
    mvn clean tomcat7:redeploy
    mvn tomcat7:deploy
    mvn tomcat7:undeploy 
    mvn tomcat7:redeploy
