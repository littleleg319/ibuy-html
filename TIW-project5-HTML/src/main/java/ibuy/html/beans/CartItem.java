package ibuy.html.beans;

public class CartItem {
		private String productid;
		private int supplierid;
		private String name;
		private Float price;
		private int qta;
		
		public String getProductId() {
			return productid;
		}

		public void setProductId(String productid) {
			this.productid = productid;
		}
		
		public int getSupplierId() {
			return supplierid;
		}

		public void setSupplierId(int supplierid) {
			this.supplierid = supplierid;
		}
		public Float getPrice() {
			return price;
		}

		public void setPrice(Float price) {
			this.price = price;
		}
		
		public int getQta() {
			return qta;
		}

		public void setQta(int qta) {
			this.qta = qta;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name=name;
		}
		
}
