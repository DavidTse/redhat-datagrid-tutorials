package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public class CurrentModificationThread extends Thread
{
	private HttpSession session;
	
	
	public CurrentModificationThread(HttpSession session)
	{
		this.session = session;
	}
	
	public void run()
	{
		HttpSessionInfo httpSessionInfo = (HttpSessionInfo) session.getAttribute("user");
		ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = httpSessionInfo.getConcurrentHashMap();
        for(int i = 1; i<6; i++) {
        	String tmp = Integer.toString(i);
        	BigInteger key = new BigInteger(tmp);
        	BigInteger value = new BigInteger(tmp);
        	concurrentHashMap.put(key, value);
        }
		
	    // It looks like the first 2 lines of this method are returning a reference
	    // for concurrentHashMap so that it does not have to be set again in the session.
	    // ConcurrentHashMap is not a transaction.
	    // Turning on near cache improves performance.
		//session.setAttribute("user", httpSessionInfo);
		System.out.println("CurrentModificationThread Done. "+System.currentTimeMillis());
	}
}
