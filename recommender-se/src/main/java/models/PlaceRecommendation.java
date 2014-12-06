package models;

public class PlaceRecommendation {
	
	private String name;
	private String url;
	private String picture;
	private String description;
	private double score;
	private String[] categories;
	private String code;
	private Location location;
	public PlaceRecommendation(String name, String url, String picture,
			String description, double score, String[] categories, String code,
			Location location) {
		super();
		this.name = name;
		this.url = url;
		this.picture = picture;
		this.description = description;
		this.score = score;
		this.categories = categories;
		this.code = code;
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
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
	public String getCode() {
		return code;
	}
	public Location getLocation() {
		return location;
	}
	
	
	

}
