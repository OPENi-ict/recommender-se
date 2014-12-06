package models;

public class ApplicationRecommendation {
	
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
	private int rating;
	private String os;
	public ApplicationRecommendation(String name, String url, String type,
			String service, String picture, String description, double score,
			String[] categories, double price, String currency, String code,
			int rating, String os) {
		super();
		this.name = name;
		this.url = url;
		this.type = type;
		this.service = service;
		this.picture = picture;
		this.description = description;
		this.score = score;
		this.categories = categories;
		this.price = price;
		this.currency = currency;
		this.code = code;
		this.rating = rating;
		this.os = os;
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
	public int getRating() {
		return rating;
	}
	public String getOs() {
		return os;
	}
	
	

}
