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
 * Thread2(PopulateAccountThread) getMessage runs longer than thread1
 * Thread3(CheckingThread) opens a Docket from a DocketList, a ConcurrentHashMap
 *
 * @author David Tse
 */
public class CurrentModificationThread extends Thread
{
	private static AtomicInteger counter = new AtomicInteger(0);
	
	private static AtomicInteger resetcounter;
	
	private HttpSession session;
	
	private int index;
	
	public CurrentModificationThread(HttpSession session, int index)
	{
		this.session= session;
		
		// Track the index for debugging
		this.index = index;
	}
	
	public void run()
	{
        String sessionID = session.getId();
        ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = new ConcurrentHashMap<>();		

		if (index == 0) resetcounter = new AtomicInteger(0);
		int resetcount = resetcounter.incrementAndGet();
		int atomiccount = counter.incrementAndGet();

		BigInteger key1 = new BigInteger("1");
		BigInteger key2 = new BigInteger("2");
		BigInteger key3 = new BigInteger("3");
		BigInteger bigatomiccount = new BigInteger(Integer.toString(atomiccount));
		BigInteger bigindex = new BigInteger(Integer.toString(index));
		BigInteger bigresetcount = new BigInteger(Integer.toString(resetcount));
		
		concurrentHashMap.put(key1, bigatomiccount);			
		concurrentHashMap.put(key2, bigindex);			
		concurrentHashMap.put(key3, bigresetcount);

		CacheManager.getCacheManager().setCacheData(sessionID, "docketList", concurrentHashMap);
		
		String debug = concurrentHashMap.toString();
		String objRef = Integer.toHexString(System.identityHashCode(concurrentHashMap));
        System.out.println("CurrentModificationThread " + index + " " + debug + " " + objRef);
	}
}
