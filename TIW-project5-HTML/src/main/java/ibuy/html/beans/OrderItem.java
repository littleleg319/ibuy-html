package ibuy.html.beans;

public class OrderItem {
	private int orderid;
	private String productid;
	private int qta;
	private Float price;
	
	public int getOrderId() {
		return orderid;
	}

	public void setOrderId(int orderid) {
		this.orderid = orderid;
	} 
	
	public int getQta() {
		return qta;
	}

	public void setQta(int qta) {
		this.qta = qta;
	} 
	
	public String getProductId() {
		return productid;
	}

	public void setProductId(String productid) {
		this.productid = productid;
	} 
	
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	} 
}
