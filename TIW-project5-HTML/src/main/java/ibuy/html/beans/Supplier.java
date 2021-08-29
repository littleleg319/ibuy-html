package ibuy.html.beans;

import java.util.List;

public class Supplier {
	private int supplierid;
	private String name;
	private float rating;
	private float freeshipping;
	private float prodPrice;
//	private List<PriceRange> range;
	
	public int getSupplierId() {
		return supplierid;
	}

	public void setSupplierId(int supplierid) {
		this.supplierid = supplierid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}
	
	public float getFreeShipping() {
		return freeshipping;
	}

	public void setFreeShipping(float freeshipping) {
		this.freeshipping = freeshipping;
	}
	
	public float getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(float prodPrice) {
		this.prodPrice = prodPrice;
	}
	
/*	public List<PriceRange> getPriceRange() {
		return range;
	}

	public void setPriceRange(List<PriceRange> range) {
		this.range = range;
	}*/
}
