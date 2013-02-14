package com.garage.ejb;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
 
@Stateless (
	name =  "TestBean", 
	mappedName =  "ejb/stateless/Test", 
	description =  "Tests a stateless session bean from the server") 
@Local ({Test.class})
@Remote ({TestRemote.class}) 

public  class TestBean { 
	public String echo(String str) {
		System.out.println("====== YE:S STATELESS SESSION BEAN ===== " + str);
		return str;
	} 
}

