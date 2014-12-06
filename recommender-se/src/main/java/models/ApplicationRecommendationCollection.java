package models;

import com.google.gson.Gson;

public class ApplicationRecommendationCollection {
	
	private ApplicationRecommendation[] collection;

	public ApplicationRecommendationCollection(
			ApplicationRecommendation[] collection) {
		super();
		this.collection = collection;
	}

	public ApplicationRecommendation[] getCollection() {
		return collection;
	}
	
    public String getCollectionAsJsonObject() {
		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}

}
