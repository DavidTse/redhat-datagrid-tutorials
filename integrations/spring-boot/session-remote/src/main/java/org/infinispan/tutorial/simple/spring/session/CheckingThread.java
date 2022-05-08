package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

/**
 * This is 1 of the 3 threads used by the WebController to duplicate 
 * a scenario reported by a Red Hat customer
 * 
 * Thread1(CurrentModificationThread) getDocketList writing to ConcurrentHashMap 
 * Thread2(PopulateAccountThread) getMessage run longer than thread1
 * Thread3(CheckingThread) opens a Docket from a DocketList, a ConcurrentHashMap
 * 
 *
 * @author David Tse
 */
public class CheckingThread extends Thread 
{

	private HttpSession session;
	
	private int index;
	
	public CheckingThread(HttpSession session, int index)
	{
		this.session = session;
		
		// Track the index for debugging
		this.index = index;
	}

	public void run()
	{
        String sessionID = session.getId();
		
		ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = 
		(ConcurrentHashMap<BigInteger, BigInteger>) CacheManager.getCacheManager().getCacheData(sessionID, "docketList");
		
        String debug = concurrentHashMap.toString();
        
		String objRef = Integer.toHexString(System.identityHashCode(concurrentHashMap));
        System.out.println("CheckingThread " + index + " " + debug + " " + objRef);
	}
}
