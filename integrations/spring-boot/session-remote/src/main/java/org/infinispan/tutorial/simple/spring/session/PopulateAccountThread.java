package org.infinispan.tutorial.simple.spring.session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

/**
 * This is 1 of the 3 threads used by the WebController to duplicate 
 * a scenario reported by a Red Hat customer
 * 
 * Thread1(CurrentModificationThread) getDocketList writing to ConcurrentHashMap 
 * Thread2(PopulateAccountThread) getMessage run longer than thread1
 * Thread3(CheckingThread) opens a Docket from a DocketList, a ConcurrentHashMap
 *
 * @author David Tse
 */
public class PopulateAccountThread extends Thread 
{
	private HttpSession session;
	
	private int index;
	
	public PopulateAccountThread(HttpSession session, int index)
	{
		this.session = session;
		
		// Track the index for debugging
		this.index = index;
	}

	public void run()
	{
        String sessionID = session.getId();
        User user = (User) CacheManager.getCacheManager().getCacheData(sessionID, "user");
        long uuid2 = Long.parseLong(user.getPhones().get(0));
        Account account = (Account) CacheManager.getCacheManager().getCacheData(sessionID, "account");
        if (account == null) {
        	account = new Account();
        }
        
		for (int i=0; i<50; i++) {
	        account.setUserID(user.getUserID());
	        UUID uuid = new UUID(2l, uuid2);
	        account.setAccountID(uuid);
	        
	        Item item1 = new Item();
	        item1.setItemRef("ISBN0201633612");
	        item1.setItemDescription("Design Patterns");
	        item1.setItemCategory("Book");
	        item1.setQuantity(1);
	        item1.setPrice(49.95);
	        
	        Item item2 = new Item();
	        item2.setItemRef("ISBN1617290343");
	        item2.setItemDescription("Big Data");
	        item2.setItemCategory("Book");
	        item2.setQuantity(1);
	        item2.setPrice(49.99);
	        
	        List<Item> itemlist1 = new ArrayList<>();
	        List<Item> itemlist2 = new ArrayList<>();
	
	        itemlist1.add(item1);
	        itemlist2.add(item2);
	
	        Order order1 = new Order();
	        uuid = new UUID(4l, uuid2);
	        order1.setOrderID(uuid);
	        order1.setItems(itemlist1);
	        
	        Order order2 = new Order();
	        uuid = new UUID(4l, uuid2++);
	        order2.setOrderID(uuid);
	        order2.setItems(itemlist2);
	        List<Order> orderlist = new ArrayList<>();
	        orderlist.add(order1);
	        orderlist.add(order2);
	        account.setOrders(orderlist);
	        account.setAccountNumber(user.getPhones().get(0));
	        account.setAccountType("Visa");
	        account.setPaymentType("Credit Card");
		}
		
		CacheManager.getCacheManager().setCacheData(sessionID, "account", account);
		
		String objRef = Integer.toHexString(System.identityHashCode(account));
        System.out.println("PopulateAccountThread " + index + " " + objRef);
	}
}
