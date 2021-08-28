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
	
	public void UpdateProductSeen(String userid, String productId)  {
		String query = "SELECT  * FROM user_product  WHERE userid = ? AND productid =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, userid);
			pstatement.setString(2, productId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) { // no results, insert new value
					String insert_seen = "INSERT INTO user_product (userid, productid, timestamp) VALUES (?, ?, CURDATE())";
					PreparedStatement ps = con.prepareStatement(insert_seen);
					ps.setString(1,userid);
					ps.setString(2, productId);
					ps.executeUpdate();
					} else {
					String update_seen = "UPDATE user_product SET timestamp=CURDATE() WHERE userid = ? AND productid = ?";
					PreparedStatement ps = con.prepareStatement(update_seen);
					ps.setString(1,userid);
					ps.setString(2, productId);
					ps.executeUpdate();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
