package control.products;

public class Product {
	
	private String product_name;
	private String product_code;
	private String image;
	private String brand_code;
	private String brand;
	private String brand_owner;
	private String gln;
	private String gln_country_code;
	private String registration;
	private String registration_iso_code;
	private String category_code;
	private String brand_image;
	private String category_name;

	
	
	public Product(String product_name, String product_code, String image,
			String brand_code, String brand, String brand_owner, String gln,
			String gln_country_code, String registration,
			String registration_iso_code, String category_code, String brand_image, String category_name ) {
		super();
		this.product_name = product_name;
		this.product_code = product_code;
		this.image = image;
		this.brand_code = brand_code;
		this.brand = brand;
		this.brand_owner = brand_owner;
		this.gln = gln;
		this.gln_country_code = gln_country_code;
		this.registration = registration;
		this.registration_iso_code = registration_iso_code;
		this.category_code = category_code;
		this.brand_image = brand_image;
		this.category_name = category_name;
	}
	
	public String getProduct_name() {
		return product_name;
	}
	public String getProduct_code() {
		return product_code;
	}
	public String getImage() {
		return image;
	}
	public String getBrand_code() {
		return brand_code;
	}
	public String getBrand() {
		return brand;
	}
	public String getBrand_owner() {
		return brand_owner;
	}
	public String getGln() {
		return gln;
	}
	public String getGln_country_code() {
		return gln_country_code;
	}
	public String getRegistration() {
		return registration;
	}
	public String getRegistration_iso_code() {
		return registration_iso_code;
	}
	public String getCategory_code() {
		return category_code;
	}
	
	public String getBrand_image() {
		return brand_image;
	}
	public String getCategory_name() {
		return category_name;
	}
	
	
	

}
