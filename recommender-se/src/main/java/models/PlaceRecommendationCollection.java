package models;

import com.google.gson.Gson;

public class PlaceRecommendationCollection {
	
	private PlaceRecommendation[] collection;

	public PlaceRecommendationCollection(PlaceRecommendation[] collection) {
		super();
		this.collection = collection;
	}

	public PlaceRecommendation[] getCollection() {
		return collection;
	}
	
    public String getCollectionAsJsonObject() {
 		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}

}
