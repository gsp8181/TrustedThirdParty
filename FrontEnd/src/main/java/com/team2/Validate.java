package com.team2;

public class Validate {
	
	public static boolean verify(String email){
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean valid = email.matches(EMAIL_REGEX);
		if(valid) {return true;}
		return false;
	}

}
