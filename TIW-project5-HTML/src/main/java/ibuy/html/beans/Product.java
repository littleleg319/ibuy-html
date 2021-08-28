package ibuy.html.beans;

import java.sql.Blob;

public class Product {
	
	private String code;
	private String name;
	private String description;
	private String category;
	private Blob photo;
	private String price;
	
	public String getCode() {
		return code;
	}

	public void setCode (String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription (String description) {
		this.description = description;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory (String category) {
		this.category = category;
	}

	public Blob getPhoto() {
		return photo;
	}

	public void setPhoto (Blob blob) {
		this.photo = blob;
	}
	
	public String getPrice() {
		return price;
	}

	public void setPrice (String price) {
		this.price = price;
	}
}
