package ibuy.html.beans;

public class Order {
	private String name;
	private Float shipcost;
	private Float price;
	private int supplierid;
	private int userid;
	private String suppliername;
	private String shipaddress;
	private String shipdate;
	private String timestamp;
	private int orderid;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public float getShipCost() {
		return shipcost;
	}

	public void setShipCost(float shipcost) {
		this.shipcost = shipcost;
	}
	
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public int getSupplierId() {
		return supplierid;
	}

	public void setSupplierId(int supplierid) {
		this.supplierid = supplierid;
	}
	
	public int getUserId() {
		return userid;
	}

	public void setUserId(int userid) {
		this.userid = userid;
	}
	
	public String getSupplierName() {
		return suppliername;
	}

	public void setSupplierName(String suppliername) {
		this.suppliername = suppliername;
	}
	
	public String getTimeStamp() {
		return timestamp;
	}

	public void setTimeStamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getOrderId() {
		return orderid;
	}

	public void setOrderId(int orderid) {
		this.orderid = orderid;
	}	
	
	public String getShipAddress() {
		return shipaddress;
	}

	public void setShipAddress(String shipaddress) {
		this.shipaddress = shipaddress;
	}
	
	public String getShipDate() {
		return shipdate;
	}

	public void setShipDate(String shipdate) {
		this.shipdate = shipdate;
	}
}
