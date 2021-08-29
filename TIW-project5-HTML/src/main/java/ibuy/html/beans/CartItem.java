package ibuy.html.beans;

public class CartItem {
		private String productid;
		private Float price;
		private int qta;
		
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
		
		public int getQta() {
			return qta;
		}

		public void setQta(int qta) {
			this.qta = qta;
		}
		
}
