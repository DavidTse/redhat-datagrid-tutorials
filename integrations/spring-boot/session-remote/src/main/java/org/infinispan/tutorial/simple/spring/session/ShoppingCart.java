package org.infinispan.tutorial.simple.spring.session;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class ShoppingCart implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8757220672042200239L;
	
	private UUID cartID;
	
	private List<Item> items;

	public ShoppingCart() {}
	
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public UUID getCartID() {
		return cartID;
	}

	public void setCartID(UUID cartID) {
		this.cartID = cartID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cartID == null) ? 0 : cartID.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		ShoppingCart other = (ShoppingCart) obj;
		if (cartID == null) {
			if (other.cartID != null)
				return false;
		} else if (!cartID.equals(other.cartID))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ShoppingCart [cartID=" + cartID + ", items=" + items + "]";
	}

}
