package ibuy.html.beans;

public class PriceRange {
	private int id;
	private int supplierid;
	private int minArt;
	private int maxArt;
	private float price;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getSupplierId() {
		return supplierid;
	}

	public void setSupplierId(int supplierid) {
		this.supplierid = supplierid;
	}
	
	public int getMinArt() {
		return minArt;
	}

	public void setMinArt(int minArt) {
		this.minArt = minArt;
	}
	
	public int getMaxArt() {
		return maxArt;
	}

	public void setMaxArt(int maxArt) {
		this.maxArt = maxArt;
	}
	
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
}
