package models;

import com.google.gson.Gson;

public class ApplicationRecommendationCollection {
	
	private ApplicationRecommendation[] recommendations;

	public ApplicationRecommendationCollection(
			ApplicationRecommendation[] collection) {
		super();
		this.recommendations = collection;
	}

	public ApplicationRecommendation[] getCollection() {
		return recommendations;
	}
	
    public String getCollectionAsJsonObject() {
		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}

}
