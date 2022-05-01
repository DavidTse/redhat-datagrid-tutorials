package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

/**
 * This is 1 of the 3 threads used by the WebController to duplicate 
 * a scenario reported by a Red Hat customer
 * 
 * Thread1(CurrentModificationThread) getDocketList writing to ConcurrentHashMap 
 * Thread2(PopulateSessionInfoThread) getMessage run longer than thread1
 * Thread3(CheckingThread) opens a Docket from a DocketList, a ConcurrentHashMap
 * 
 * A better solution to simulate the effect of 500 login/logout is to use a
 * for loop and HttpURLConnection
 * <p>
 * see https://github.com/DavidTse/datagrid, HttpURLConnectionLoadTest.java
 * <p>
 *
 * @author David Tse
 */
public class CheckingThread extends Thread 
{
	//private HttpSession session;
	//Solution: holds a reference to the top level container instead of Session
	private HttpSessionInfo httpSessionInfo;
	
	private int index;
	
	public CheckingThread(HttpSession session, int index)
	{
		//this.httpSessionInfo = session;
		//Solution: holds a reference to the top level container instead of session
		httpSessionInfo = (HttpSessionInfo) session.getAttribute("user");
		
		// Track the index for debugging
		this.index = index;
	}

	public void run()
	{
		// Get object reference
		String objRef = Integer.toHexString(System.identityHashCode(httpSessionInfo));
		
	    // Use the reference from httpSessionInfo to populate the concurrentHashMap
		ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = httpSessionInfo.getConcurrentHashMap();
		
        String debug = concurrentHashMap.toString();
        
        System.out.println("CheckingThread "+ debug + " " + index + " " + objRef);
	}
}
