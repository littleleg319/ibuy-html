package ibuy.html.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ibuy.html.beans.PriceRange;
import ibuy.html.beans.Product;
import ibuy.html.beans.Supplier;

public class SupplierDAO {
	private Connection con;

	public SupplierDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Supplier> findSupplierDetails(String productid) throws SQLException{
		String supplier = "SELECT * FROM supplier as s , supplier_product_price as p WHERE p.idproduct = ? AND p.idsupplier = s.supplierid ";
		List<Supplier> suppliers = new ArrayList<Supplier>();
		try (PreparedStatement ps = con.prepareStatement(supplier);) {
			ps.setString(1,productid);
			ResultSet res = ps.executeQuery();
			if (!res.isBeforeFirst())
				return null;
			else {
				while(res.next()) {
					Supplier sup = new Supplier();
					sup.setSupplierId(res.getInt("supplierid"));
					sup.setName(res.getString("name"));
					sup.setRating(res.getFloat("rating"));
					sup.setFreeShipping(res.getFloat("freeship"));
					sup.setProdPrice(res.getFloat("price"));
					sup.setPriceRange(findShippingRanges(res.getInt("supplierid")));
					suppliers.add(sup);
				}
			}
		}
		return suppliers;
	}
	
	public List<PriceRange> findShippingRanges (int supplierid) throws SQLException {
		String query = "SELECT * FROM pricerange WHERE supplierid = ?";
		List<PriceRange> ranges = new ArrayList<PriceRange>();
		try (PreparedStatement ps = con.prepareStatement(query);) {
			String idsuppl = String.valueOf(supplierid);
			ps.setString(1,idsuppl);
			ResultSet res = ps.executeQuery();
			if (!res.isBeforeFirst())
				return null;
			else {
				while(res.next()) {
					PriceRange rg = new PriceRange();
					rg.setId(res.getInt("id"));
					rg.setMinArt(res.getInt("minart"));
					rg.setMaxArt(res.getInt("maxart"));
					rg.setPrice(res.getFloat("price"));
					ranges.add(rg);
				}
			}
		} return ranges;
	}
}
