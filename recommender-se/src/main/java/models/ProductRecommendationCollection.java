package models;

import com.google.gson.Gson;

public class ProductRecommendationCollection {
	
	private ProductRecommendation[] recommendations;

	public ProductRecommendationCollection(ProductRecommendation[] collection) {
		super();
		this.recommendations = collection;
	}

	public ProductRecommendation[] getCollection() {
		return recommendations;
	}
	
	public String getCollectionAsJsonObject() {
		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}

}
