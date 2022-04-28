package org.infinispan.tutorial.simple.spring.session;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class HttpSessionInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5017611248203683707L;
	
	private String sessionKey;
	
	private transient CyclicBarrier userBarrier;
	
	private transient CyclicBarrier accountBarrier;
	
	private transient CyclicBarrier shoppingCartBarrier;
	
	private transient CyclicBarrier concurrentHashMapBarrier;
	
	private User user;
	
	private Account account;
	
	private ShoppingCart shoppingCart;
	
	private ConcurrentHashMap<BigInteger, BigInteger> concurrentHashMap;
	
	public HttpSessionInfo() {
		user = new User();
		account = new Account();
		shoppingCart = new ShoppingCart();
		concurrentHashMap = new ConcurrentHashMap<>();
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public User getUser() {
		return user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public ConcurrentHashMap<BigInteger, BigInteger> getConcurrentHashMap() {
		return concurrentHashMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((concurrentHashMap == null) ? 0 : concurrentHashMap.hashCode());
		result = prime * result + ((sessionKey == null) ? 0 : sessionKey.hashCode());
		result = prime * result + ((shoppingCart == null) ? 0 : shoppingCart.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpSessionInfo other = (HttpSessionInfo) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (concurrentHashMap == null) {
			if (other.concurrentHashMap != null)
				return false;
		} else if (!concurrentHashMap.equals(other.concurrentHashMap))
			return false;
		if (sessionKey == null) {
			if (other.sessionKey != null)
				return false;
		} else if (!sessionKey.equals(other.sessionKey))
			return false;
		if (shoppingCart == null) {
			if (other.shoppingCart != null)
				return false;
		} else if (!shoppingCart.equals(other.shoppingCart))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HttpSessionInfo [sessionKey=" + sessionKey + ", user=" + user + ", account=" + account
				+ ", shoppingCart=" + shoppingCart + ", concurrentHashMap=" + concurrentHashMap + "]";
	}
}
