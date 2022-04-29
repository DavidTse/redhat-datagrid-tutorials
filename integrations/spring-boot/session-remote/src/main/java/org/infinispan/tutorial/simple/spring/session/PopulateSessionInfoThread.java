package org.infinispan.tutorial.simple.spring.session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

public class PopulateSessionInfoThread extends Thread 
{
	private HttpSession session;
	
	public PopulateSessionInfoThread(HttpSession session)
	{
		this.session = session;
	}

	public void run()
	{
		HttpSessionInfo httpSessionInfo = (HttpSessionInfo) session.getAttribute("user");
		
		User user = httpSessionInfo.getUser();
		String name = (String) session.getAttribute("latest");
		String email = (String) session.getAttribute("email");
		String phone = (String) session.getAttribute("phone");
        long uuid2 = Long.parseLong(phone);
        
        UUID uuid = new UUID(1l, uuid2);
        user.setUserID(uuid);
        user.setUserName(name);

        String emailAddress = name +"@" + email + ".com";
        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        emails.add(emailAddress);
        user.setEmails(emails);
        phones.add(phone);
        user.setPhones(phones);

        
        Account account = httpSessionInfo.getAccount();
        account.setUserID(uuid);
        uuid = new UUID(2l, uuid2);
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
        account.setAccountNumber(phone);
        account.setAccountType("Visa");
        account.setPaymentType("Credit Card");
        httpSessionInfo.setAccount(account);
        
        ShoppingCart shoppingCart = httpSessionInfo.getShoppingCart();
        uuid = new UUID(3l, uuid2);
        shoppingCart.setCartID(uuid);

        item1 = new Item();
        item1.setItemRef("ISBN9781119254010");
        item1.setItemDescription("Spark");
        item1.setItemCategory("Book");
        item1.setQuantity(1);
        item1.setPrice(49.95);
        
        item2 = new Item();
        item2.setItemRef("ISBN9781491912768");
        item2.setItemDescription("Advanced Analytics with Spark");
        item2.setItemCategory("Book");
        item2.setQuantity(1);
        item2.setPrice(49.99);
        
        List<Item> itemlist3 = new ArrayList<>();
        itemlist3.add(item1);
        itemlist3.add(item2);
        shoppingCart.setItems(itemlist3);

        ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap = httpSessionInfo.getConcurrentHashMap();
        for (Entry<BigInteger, BigInteger> entry : concurrentHashMap.entrySet()) {
        	System.out.println("PopulateSessionInfoThread::Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        
        /*
        concurrentHashMap.clear();
        for(int i = 1; i<6; i++) {
        	String tmp = Integer.toString(i);
        	BigInteger key = new BigInteger(tmp);
        	BigInteger value = key.multiply(key);
        	concurrentHashMap.put(key, value);
        }
        */
        System.out.println("PopulateSessionInfoThread Done. "+System.currentTimeMillis());
	}
}
