package com.garage.ejb;

import  java.rmi.RemoteException;
import  java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import  javax.naming.NamingException;

import com.garage.ejb.SessionTestRemote;
import com.garage.ejb.lab.TestRemote;

public  class TestEJBClient {

	public static void main(String[] args) throws RemoteException {
		TestEJBClient client = new TestEJBClient();
		client.testSessionBean();
	}
	public void testSessionBean() throws RemoteException {

		/*
		* Create a JNDI API InitialContext object if none exists yet.
		*/
		
		Context jndiContext = 	null; 
		
		try {
		
			Properties env = 	new Properties( );
			env.put(Context.SECURITY_PRINCIPAL, "weblogic"); 
			env.put(Context.SECURITY_CREDENTIALS, "weblogic1");
			env.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
			env.put(Context.PROVIDER_URL,"t3://localhost:7001"); 
			jndiContext =new InitialContext(env);	
		} catch (NamingException e) {	
			System.out.println("Could not create JNDI API context: " +	
			e.toString());	
			System.exit(1);	
		} 
		
		TestRemote remoteObj = null;
		try {	
			remoteObj = (TestRemote) jndiContext.lookup("ejb/stateless/Test#com.garage.lab.TestRemote");
			System.out.println("======="+remoteObj.echo("SHYAM TestRemote RETEST"));
		} catch (Exception e) {	
			System.out.println("JNDI API lookup failed: " + e.toString());	
			e.printStackTrace();
			System.exit(1);	
		} 
		
		SessionTestRemote remoteObj1 = null;
		try {		
			remoteObj1 = (SessionTestRemote) jndiContext.lookup(	
			"ejb/stateless/SessionTest#com.garage.SessionTestRemote");	
			System.	out.println("======="+remoteObj1.echo("SHYAM SessionTestRemote"));		
		} catch (Exception e) {	
			System.out.println("JNDI API lookup failed: " + e.toString());
			e.printStackTrace();	
			System.exit(1);	
		} 
	}//Test session
} // [CLASS - ]
