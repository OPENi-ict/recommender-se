package models;

public class ApplicationRecommendation {
	
	private String title;
	private String url;
	private String version;
	private String icon;
	private String description;
	private double price;
	private String currency;
	private int rating;
	private String os;
	private String[] categories;
	private String developer;
	
	public ApplicationRecommendation(String title, String url, String version,
			String icon, String description, double price, String currency,
			int rating, String os, String[] categories, String developer) {
		super();
		this.title = title;
		this.url = url;
		this.version = version;
		this.icon = icon;
		this.description = description;
		this.price = price;
		this.currency = currency;
		this.rating = rating;
		this.os = os;
		this.categories = categories;
		this.developer = developer;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getVersion() {
		return version;
	}

	public String getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}

	public double getPrice() {
		return price;
	}

	public String getCurrency() {
		return currency;
	}

	public int getRating() {
		return rating;
	}

	public String getOs() {
		return os;
	}

	public String[] getCategories() {
		return categories;
	}

	public String getDeveloper() {
		return developer;
	}
	
	
	

}
