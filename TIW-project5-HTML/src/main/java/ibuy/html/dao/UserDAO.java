package ibuy.html.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ibuy.html.beans.User;
import ibuy.html.beans.Product;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, username, name, surname, email, shipmentaddress FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setEmail(result.getString("email"));
					user.setShipmentaddress(result.getString("shipmentaddress"));
					return user;
				}
			}
		}
	}
	
	public List<Product> findLastProductByUser(String userid) throws SQLException {
		List<Product> prods = new ArrayList<Product>();
		String check = "SELECT count(*) as total FROM user_product  WHERE userid = ?";
		try (PreparedStatement ps = con.prepareStatement(check);){
			ps.setString(1, userid);
			ResultSet res = ps.executeQuery();
			if (res.isBeforeFirst())
				return null;
			else {
		int s = res.getInt("total");
		if ( s >= '5') {
		String query1 = "SELECT code, name FROM product INNER JOIN user_product ON product.code = user_product.productid  WHERE user_product.userid = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query1);) {
			pstatement.setString(1, userid);
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

	public int findmissingProd(String userid) throws SQLException {
		int s = 0;
		String check = "SELECT count(*) as total FROM user_product  WHERE userid = ?";
		try (PreparedStatement ps = con.prepareStatement(check);){
			ps.setString(1, userid);
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
