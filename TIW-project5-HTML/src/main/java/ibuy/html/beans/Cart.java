package ibuy.html.beans;

public class Cart {
	private int supplierid;
	private float fee;
	private float totalCost;
	private int totalQta;
	private String name;
	private float freeship;
	
	public int getSupplierId() {
		return supplierid;
	}

	public void setSupplierId(int supplierid) {
		this.supplierid = supplierid;
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public int getTotalQta() {
		return totalQta;
	}

	public void setTotalQta(int totalQta) {
		this.totalQta = totalQta;
	}
	
	public void setFreeShip(float freeship) {
		this.freeship = freeship;
	}
	
	public float getFreeShip() {
		return freeship;
	}
}
