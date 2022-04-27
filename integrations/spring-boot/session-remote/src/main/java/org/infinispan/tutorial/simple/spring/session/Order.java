package org.infinispan.tutorial.simple.spring.session;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Order implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6167521735573309168L;
	
	private UUID orderID;
	
	private List<Item> items;
	
	public Order() {}

	public UUID getOrderID() {
		return orderID;
	}

	public void setOrderID(UUID orderID) {
		this.orderID = orderID;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((orderID == null) ? 0 : orderID.hashCode());
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
		Order other = (Order) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (orderID == null) {
			if (other.orderID != null)
				return false;
		} else if (!orderID.equals(other.orderID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [orderID=" + orderID + ", items=" + items + "]";
	}
}
