package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

public class CurrentModificationThread extends Thread
{
	private static AtomicInteger counter = new AtomicInteger(0);
	
	private static AtomicInteger resetcounter;
	
	private HttpSession session;
	
	private int index;
	
	public CurrentModificationThread(HttpSession session, int index)
	{
		this.session = session;
		this.index = index;
	}
	
	public void run()
	{
		HttpSessionInfo httpSessionInfo = (HttpSessionInfo) session.getAttribute("user");
		ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = httpSessionInfo.getConcurrentHashMap();

		if (index == 0) resetcounter = new AtomicInteger(0);
		int resetcount = resetcounter.incrementAndGet();
		int atomiccount = counter.incrementAndGet();
		
		String debug = null;
		BigInteger key0 = new BigInteger("0");
		BigInteger increment = new BigInteger("1");
		BigInteger key1 = new BigInteger("1");
		BigInteger key2 = new BigInteger("2");
		BigInteger key3 = new BigInteger("3");
		BigInteger bigatomiccount = new BigInteger(Integer.toString(atomiccount));
		BigInteger bigindex = new BigInteger(Integer.toString(index));
		BigInteger bigresetcount = new BigInteger(Integer.toString(resetcount));
		
		synchronized (concurrentHashMap) {
			int size = concurrentHashMap.size();
			if (size == 0) {
			    for(int i = 0; i<5; i++) {
			    	String tmp = Integer.toString(i);
			    	BigInteger key = new BigInteger(tmp);
			    	BigInteger value = new BigInteger(tmp);
			    	concurrentHashMap.put(key, value);
			    }
			}
	        
			BigInteger counter = concurrentHashMap.get(key0);
			counter = counter.add(increment);
			concurrentHashMap.put(key0, counter);
			concurrentHashMap.put(key1, bigatomiccount);			
			concurrentHashMap.put(key2, bigindex);			
			concurrentHashMap.put(key3, bigresetcount);
			
			debug = concurrentHashMap.toString();
		}
		String objRef = Integer.toHexString(System.identityHashCode(concurrentHashMap));
	    // It looks like the first 2 lines of this method are returning a reference
	    // for concurrentHashMap so that it does not have to be set again in the session.
	    // ConcurrentHashMap does not support write lock.
	    // Turning on near cache improves performance.
		//session.setAttribute("user", httpSessionInfo);
		System.out.println("CurrentModificationThread: " + debug + ". " +objRef);
	}
}
