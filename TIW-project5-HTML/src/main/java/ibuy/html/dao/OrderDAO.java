package ibuy.html.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.swing.text.DateFormatter;

import ibuy.html.beans.Cart;
import ibuy.html.beans.CartItem;

public class OrderDAO {
	private Connection con;

	public OrderDAO(Connection connection) {
		this.con = connection;
	}
		
	public int CreateOrder(int userid, List<CartItem> items, Cart cart)  {
		//diachiarazione variabli
		String address = null;
		Integer esito = 1;
		String suppl_name = null;
		Float price = (float) 0;
		Float shippingCost = (float) 0;
		Float totalCost = (float) 0;
		Integer totalQta = 0;
		Integer supplierid = null;
		String name = null;
		String order_id = null;
		
	
		
		//Retrieve dati
		String usr = String.valueOf(userid);
		suppl_name = cart.getName();
		price = cart.getTotalCost();
		shippingCost = cart.getFee();
		supplierid = cart.getSupplierId();
		totalQta = cart.getTotalQta();
		
		//calcolo prezzo finale
		totalCost = price + shippingCost;
		
		//Set date e timestamp
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter mydate = DateTimeFormatter.ofPattern("yyyMMddHHmmss");
		DateTimeFormatter dateonly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		String order_date = date.format(mydate);
		String timestamp = date.format(datetime);
		String data = date.format(dateonly);
		
		
		//composizione nome ordine
		name = "OD" + usr + supplierid + order_date;
		
		//Calcolo shipping date
		LocalDateTime shipDate = date.plusHours(24);
		String shippingDate = shipDate.format(dateonly);
		
		//Cerco l'indirizzo di spedizione utente
		String shipaddress = "SELECT shipmentaddress FROM user WHERE id = ?";
		try (PreparedStatement ps_shipaddr = con.prepareStatement(shipaddress);) {
			ps_shipaddr.setString(1, usr);
			try (ResultSet result = ps_shipaddr.executeQuery();) {
				if (!result.isBeforeFirst()) { // no results --> error
					return esito;
					} else {
					while (result.next()) {
						address = result.getString("shipmentaddress");
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return esito;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return esito;
		}
		
		//Creo l'ordine
		String order = "INSERT INTO orders SET ord_name = ?, shipcost = ?, price=?, supplierid=?, userid=?, suppliername=?, shippingdate=?, shipaddress=?, timestmp=?";
			try {
			PreparedStatement ord_create = con.prepareStatement(order);
			ord_create.setString(1,name);
			ord_create.setString(2,String.valueOf(shippingCost));
			ord_create.setString(3, String.valueOf(price));
			ord_create.setString(4,String.valueOf(supplierid));
			ord_create.setString(5,usr);
			ord_create.setString(6,suppl_name);
			ord_create.setString(7,shippingDate);
			ord_create.setString(8,address);
			ord_create.setString(9,timestamp);
			ord_create.executeUpdate(); 	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return esito;
		}
		//cerco l'id del mio ordine
			String idorder = "SELECT id_order FROM orders WHERE ord_name = ? AND timestmp = ?";
			try {
				PreparedStatement ord_id = con.prepareStatement(idorder);
				ord_id.setString(1, name);
				ord_id.setString(2, timestamp);
				try (ResultSet result = ord_id.executeQuery();) {
					if (!result.isBeforeFirst()) { // no results --> error
						return esito;
						} else {
						while (result.next()) {
							order_id = result.getString("id_order");
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return esito;
				}	
			//aggiorno product_order table
		
			String order_prods= "INSERT INTO product_order (id_product, qta, price, id_order) VALUES (?,?,?,?)";
			try {	
				for (CartItem i : items) {
				PreparedStatement order_product = con.prepareStatement(order_prods);
				order_product.setString(1, i.getProductId());
				order_product.setString(2,String.valueOf(i.getQta()));
				order_product.setString(3, String.valueOf(i.getPrice()));
				order_product.setString(4, order_id);
				order_product.executeUpdate();
			}
				esito = 0;
				return esito;
			
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return esito;
			}
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return esito;
			}
	}
}
