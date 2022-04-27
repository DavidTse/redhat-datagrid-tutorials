package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public class CurrentModificationThread extends Thread
{
	private HttpSession session;
	
	private BigInteger index;
	
	public CurrentModificationThread(HttpSession session,
			                         String index)
	{
		this.session = session;
		this.index = new BigInteger(index);
	}
	
	public void run()
	{
		HttpSessionInfo httpSessionInfo = (HttpSessionInfo) session.getAttribute("user");
		ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = httpSessionInfo.getConcurrentHashMap();
		BigInteger key = new BigInteger("5");
		BigInteger value = concurrentHashMap.get(key);
		BigInteger value1 = value.add(index);
		concurrentHashMap.put(key, value1);
		value = concurrentHashMap.get(index);
		value = value.add(index);
		concurrentHashMap.put(index, value);
		
	    // It looks like the first 2 lines of this method are returning a reference
	    // for concurrentHashMap so that it does not have to be set again in the session.
	    // ConcurrentHashMap is not a transaction.
	    // Turning on near cache improves performance.
		//session.setAttribute("user", httpSessionInfo);
		//System.out.println("Thread: " + index + ", Index 5: " + value +", New Value: " + value1);
	}
}
