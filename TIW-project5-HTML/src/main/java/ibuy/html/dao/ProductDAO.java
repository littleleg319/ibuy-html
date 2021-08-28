package ibuy.html.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ibuy.html.beans.Product;
import ibuy.html.beans.User;

public class ProductDAO {
	private Connection con;

	public ProductDAO(Connection connection) {
		this.con = connection;
	}
		public List<Product> findProductsByDefaultCat (int missing) throws SQLException {
			int number = 5 - missing;
			List<Product> prods = new ArrayList<Product>();
			String value = String.valueOf(number);
			String query = "SELECT * FROM product  WHERE category = ? ORDER BY rand() limit 0,?";
			String category = "Gym Equipment";
			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setString(1, category);
				pstatement.setInt(2, number);
				try (ResultSet result = pstatement.executeQuery();) {
					if (!result.isBeforeFirst()) // no results. Something wrong
						return null;
					else {
						while (result.next()) {
							Product prod = new Product();
							prod.setCode(result.getString("code"));
							prod.setName(result.getString("name"));
							prod.setDescription(result.getString("description"));
							prod.setCategory(result.getString("category"));
							prods.add(prod);
						}
					}
				}
			}
			return prods;
		}
		
		public List<Product> findLastProductByUser(int userid) throws SQLException {
			List<Product> prods = new ArrayList<Product>();
			String usr = String.valueOf(userid);
			String check = "SELECT count(*) as total FROM user_product  WHERE userid = ?";
			try (PreparedStatement ps = con.prepareStatement(check);){
				ps.setString(1, usr);
				ResultSet res = ps.executeQuery();
				if (res.isBeforeFirst())
					return null;
				else {
			int s = res.getInt("total");
			if ( s >= '5') {
			String query1 = "SELECT code, name FROM product INNER JOIN user_product ON product.code = user_product.productid  WHERE user_product.userid = ? ";
			try (PreparedStatement pstatement = con.prepareStatement(query1);) {
				pstatement.setString(1, usr);
				try (ResultSet result = pstatement.executeQuery();) {
					if (!result.isBeforeFirst()) // no results, credential check failed
						return null;
					else {
						while(result.next()) {
						Product prod = new Product();
						prod.setCode(result.getString("code"));
						prod.setName(result.getString("name"));
						prods.add(prod);
						}
					}
				}
			}
			}
		}
	}
			return prods;
	}

		public int findmissingProd(int userid) throws SQLException {
			int s = 0;
			String check = "SELECT count(*) as total FROM user_product  WHERE userid = ?";
			try (PreparedStatement ps = con.prepareStatement(check);){
				String usr = String.valueOf(userid);
				ps.setString(1, usr);
				ResultSet res = ps.executeQuery();
				if (res.isBeforeFirst())
					return s;
				else {
						res.getInt("total");
				}
			}
			return s;
		}
}
