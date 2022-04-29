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
	        
			BigInteger key = new BigInteger("0");
			BigInteger counter = concurrentHashMap.get(key);
			counter = counter.add(new BigInteger("1"));
			concurrentHashMap.put(key, counter);
			
			key = new BigInteger("1");
			concurrentHashMap.put(key, new BigInteger(Integer.toString(atomiccount)));
			
			key = new BigInteger("2");
			concurrentHashMap.put(key, new BigInteger(Integer.toString(index)));
			
			key = new BigInteger("3");
			concurrentHashMap.put(key, new BigInteger(Integer.toString(resetcount)));
			
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
