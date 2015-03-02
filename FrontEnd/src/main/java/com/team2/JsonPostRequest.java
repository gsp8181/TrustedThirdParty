package com.team2;

import java.io.OutputStreamWriter;

import javax.json.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonPostRequest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
//		JsonObject cred = new JsonObject();
//		JsonObject auth=new JsonObject();
//		JsonObject parent=new JsonObject();
//		cred.put("username","adm");
//		cred.put("password", "pwd");
//		auth.put("tenantName", "adm");
//		auth.put("passwordCredentials", cred);
//		parent.put("auth", auth);
		
		
		
		 JsonObject value = Json.createObjectBuilder()
			     .add("firstName", "John")
			     .add("lastName", "Smith")
			     .add("age", 25)
			     .add("address", Json.createObjectBuilder()
			         .add("streetAddress", "21 2nd Street")
			         .add("city", "New York")
			         .add("state", "NY")
			         .add("postalCode", "10021"))
			     .add("phoneNumber", Json.createArrayBuilder()
			         .add(Json.createObjectBuilder()
			             .add("type", "home")
			             .add("number", "212 555-1234"))
			         .add(Json.createObjectBuilder()
			             .add("type", "fax")
			             .add("number", "646 555-4567")))
			     .build();
		 


		OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
//		wr.write(parent.toString());

	}
	
	
	public static void test(){
		
		
		
	}

}
