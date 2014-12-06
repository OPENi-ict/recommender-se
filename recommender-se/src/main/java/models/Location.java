package models;

public class Location {
	
	private double lat;
	private double lng;
	private double distance;
	private String cc;
	private String country;
	public Location(double lat, double lng, double distance, String cc,
			String country) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.distance = distance;
		this.cc = cc;
		this.country = country;
	}
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
	public double getDistance() {
		return distance;
	}
	public String getCc() {
		return cc;
	}
	public String getCountry() {
		return country;
	}
	
	
    
}
