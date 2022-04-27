package org.infinispan.tutorial.simple.spring.session;

import java.io.Serializable;

public class Item implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2699183725606044049L;
	
	String itemRef;
	
	String itemDescription;
	
	String itemCategory;
	
	int quantity;
	
	double price;

	public Item() {}

	public String getItemRef() {
		return itemRef;
	}

	public void setItemRef(String itemRef) {
		this.itemRef = itemRef;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemCategory == null) ? 0 : itemCategory.hashCode());
		result = prime * result + ((itemDescription == null) ? 0 : itemDescription.hashCode());
		result = prime * result + ((itemRef == null) ? 0 : itemRef.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + quantity;
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
		Item other = (Item) obj;
		if (itemCategory == null) {
			if (other.itemCategory != null)
				return false;
		} else if (!itemCategory.equals(other.itemCategory))
			return false;
		if (itemDescription == null) {
			if (other.itemDescription != null)
				return false;
		} else if (!itemDescription.equals(other.itemDescription))
			return false;
		if (itemRef == null) {
			if (other.itemRef != null)
				return false;
		} else if (!itemRef.equals(other.itemRef))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Item [itemRef=" + itemRef + ", itemDescription=" + itemDescription + ", itemCategory=" + itemCategory
				+ ", quantity=" + quantity + ", price=" + price + "]";
	}

}
