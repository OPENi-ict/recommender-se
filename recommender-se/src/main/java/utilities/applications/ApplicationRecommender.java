package utilities.applications;

import models.ApplicationRecommendation;
import models.ApplicationRecommendationCollection;

public class ApplicationRecommender {

	public static String getRecommendation(String authorization){

		ApplicationRecommendation pr1 = new ApplicationRecommendation("Final Fury 7", "http://ff7.games", "2.7", "N/A", "An immersive fps on a futuristic Tokyo setting", 5, "dollars", 4, "ios,android", new String[]{"Games,FPS"}, "Simusoft");
		ApplicationRecommendation pr2 = new ApplicationRecommendation("Total soccer 2015", "www", "18", "-", "A realistic football management game updated with the current line-ups of all your favorite teams", 4, "dollars", 3, "android", new String[]{"Games","Football Management"}, "A.E.");
		ApplicationRecommendation[] pin = {pr1,pr2};
		ApplicationRecommendationCollection collection = new ApplicationRecommendationCollection(pin);

		return collection.getCollectionAsJsonObject();
	}

}
