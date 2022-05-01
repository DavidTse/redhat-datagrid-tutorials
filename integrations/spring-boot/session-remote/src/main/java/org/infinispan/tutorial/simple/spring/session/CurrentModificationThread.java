package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

/**
 * This is 1 of the 3 threads used by the WebController to duplicate 
 * a scenario reported by a Red Hat customer
 * 
 * Thread1(CurrentModificationThread) getDocketList writes to ConcurrentHashMap 
 * Thread2(PopulateSessionInfoThread) getMessage runs longer than thread1
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
public class CurrentModificationThread extends Thread
{
	private static AtomicInteger counter = new AtomicInteger(0);
	
	private static AtomicInteger resetcounter;
	
	//private HttpSession session;
	//Solution: holds a reference to the top level container instead of Session
	private HttpSessionInfo httpSessionInfo;
	
	private int index;
	
	public CurrentModificationThread(HttpSession session, int index)
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

		if (index == 0) resetcounter = new AtomicInteger(0);
		int resetcount = resetcounter.incrementAndGet();
		int atomiccount = counter.incrementAndGet();
		
		//String debug = null;
		BigInteger key1 = new BigInteger("1");
		BigInteger key2 = new BigInteger("2");
		BigInteger key3 = new BigInteger("3");
		BigInteger bigatomiccount = new BigInteger(Integer.toString(atomiccount));
		BigInteger bigindex = new BigInteger(Integer.toString(index));
		BigInteger bigresetcount = new BigInteger(Integer.toString(resetcount));
		
		concurrentHashMap.put(key1, bigatomiccount);			
		concurrentHashMap.put(key2, bigindex);			
		concurrentHashMap.put(key3, bigresetcount);

		// Turning on near cache improves performance.
		//session.setAttribute("user", httpSessionInfo);
		System.out.println("CurrentModificationThread " + index + " " + objRef);
	}
}
