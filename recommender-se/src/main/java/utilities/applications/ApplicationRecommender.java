package utilities.applications;

import models.ApplicationRecommendation;
import models.ApplicationRecommendationCollection;

public class ApplicationRecommender {

	public static String getRecommendation(String authorization){

		String[] categs = {"category_1","category_2"};
		ApplicationRecommendation pr1 = new ApplicationRecommendation("product1", "www", "png", "-", "-", "-", 3.4,categs,0, "1234","-", 2,"123");
		ApplicationRecommendation pr2 = new ApplicationRecommendation("product1", "www", "png", "-", "-", "-", 3.4,categs,0, "1234","-", 2,"123");
		ApplicationRecommendation[] pin = {pr1,pr2};
		ApplicationRecommendationCollection collection = new ApplicationRecommendationCollection(pin);

		return collection.getCollectionAsJsonObject();
	}

}
