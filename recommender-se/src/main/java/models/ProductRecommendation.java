package models;

public class ProductRecommendation {
	
	private String name;
	private String url;
	private String type;
	private String service;
	private String picture;
	private String description;
	private double score;
	private String[] categories;
	private double price;
	private String currency;
	private String code;
	
	public ProductRecommendation(String name, String url, String picture,
			double score, String[] categories, String code) {
		super();
		this.name = name;
		this.url = url;
		this.picture = picture;
		this.score = score;
		this.categories = categories;
		
		this.type = "product";
		this.service = "demo";
		this.description = "N/A";
		this.price = 0;
		this.currency = "N/A";
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getType() {
		return type;
	}

	public String getService() {
		return service;
	}

	public String getPicture() {
		return picture;
	}

	public String getDescription() {
		return description;
	}

	public double getScore() {
		return score;
	}

	public String[] getCategories() {
		return categories;
	}

	public double getPrice() {
		return price;
	}

	public String getCurrency() {
		return currency;
	}
	
	public String getCode() {
		return code;
	}
	

}
