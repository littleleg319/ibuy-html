package ibuy.html.beans;

import java.util.List;

public class Cart {
	private int supplierid;
	private List<CartItem> item;
	private float fee;
	private float totalCost;
	
	public int getSupplierId() {
		return supplierid;
	}

	public void setSupplierId(int supplierid) {
		this.supplierid = supplierid;
	}
	
	public List<CartItem> getItem() {
		return item;
	}

	public void setItem(List<CartItem> item) {
		this.item = item;
	}
	
	public float getFee() {
		return fee;
	}

	public void setFee(float fee) {
		this.fee = fee;
	}
	
	public float getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}
}
