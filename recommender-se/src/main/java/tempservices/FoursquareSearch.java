package tempservices;

import java.util.HashMap;
import javax.ws.rs.client.ClientBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class FoursquareSearch {

	private String auth;
	private HashMap<String, String> categoryMap; // category name to id for fsq
	// categories

	public FoursquareSearch() {

		FSQ_Authorization frsqAuth = new FSQ_Authorization();
		auth = frsqAuth.getToken();
		categoryMap = new HashMap<String, String>();

		String responseEntity = ClientBuilder.newClient()
				.target("https://api.foursquare.com")
				.path("v2/venues/categories").queryParam("v", "20131016")
				.queryParam("oauth_token", auth).request()
				.get(String.class);

		parseCategories(responseEntity);
	}

	private void parseCategories(String response) {

		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(response);
		JsonObject jobject = json.getAsJsonObject();
		JsonObject jarray = jobject.getAsJsonObject("response");
		JsonArray jarray1 = jarray.getAsJsonArray("categories");
		for (int i = 0; i < jarray1.size(); i++) {
			addCategs(jarray1);

		}

	}

	private void addCategs(JsonArray jarray1) {
		for (int i = 0; i < jarray1.size(); i++) {
			JsonObject jo = jarray1.get(i).getAsJsonObject();
			String name = jo.get("name").getAsString();
			String id = jo.get("id").getAsString();
			categoryMap.put(name, id);
			if (jo.has("categories")) {
				JsonArray jar = jo.getAsJsonArray("categories");
				addCategs(jar);
			}
		}
	}

	private String parseRecommendedPlacesWithPhoto(String response) {

		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(response);
		JsonObject jobject = json.getAsJsonObject();
		JsonObject jarray = jobject.getAsJsonObject("response");
		JsonArray jarray1 = jarray.getAsJsonArray("venues");
		JsonArray bigA = new JsonArray();

		for (int i = 0; i < jarray1.size(); i++) {
			JsonObject job = jarray1.get(i).getAsJsonObject();

			JsonObject obj = new JsonObject();
			if (job.has("name"))
				obj.addProperty("name", job.get("name").getAsString());
			if (job.has("url"))
				obj.addProperty("url", job.get("url").getAsString());
			job.addProperty("service", "Foursquare");
			job.addProperty("score", "1");
			if (job.has("location"))
				obj.add("location", job.get("location").getAsJsonObject());
			if (job.has("categories")) {
				JsonArray catarr = job.get("categories").getAsJsonArray();
				JsonArray newArray = new JsonArray();
				for (int j = 0; j < catarr.size(); j++) {
					JsonObject cata = catarr.get(j).getAsJsonObject();
					JsonPrimitive element = new JsonPrimitive(cata.get("name")
							.getAsString());
					newArray.add(element);
				}
				obj.add("categories", newArray);
			}
			// if (job.has("id")){
			// String photoUrl = getVenuePhoto(job.get("id").getAsString());
			obj.addProperty("picture", "url_placeholder");
			obj.addProperty("description", "place description");
			obj.addProperty("score", 2);
			// // obj.addProperty("photoURL",
			// "https://irs3.4sqi.net/img/general/300x300/8744718_2hmEpnGsGt2F_4qg70uH20lfaJYPp9lmRf0RQs6p-BQ.jpg");
			// }
			bigA.add(obj);

		}
		JsonObject responseJson = new JsonObject();
		responseJson.add("recommendations", bigA);
		Gson jsonFinal = new Gson();
		String result = jsonFinal.toJson(responseJson);
		return result;
	}

	private String parsePhotoResponse(String response) {

		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(response);
		JsonObject jobject = json.getAsJsonObject();
		JsonObject jresponse = jobject.getAsJsonObject("response");
		JsonObject jphotos = jresponse.getAsJsonObject("photos");

		JsonArray jitems = jphotos.getAsJsonArray("items");
		if (jitems.size() == 0)
			return "";
		JsonObject item = jitems.get(0).getAsJsonObject();
		String prefix = item.get("prefix").getAsString();
		String suffix = item.get("suffix").getAsString();
		String photoUrl = prefix + "300x300" + suffix;
		//
		return photoUrl;

	}

	public String getFsquareNearbyVenues(double latitude, double longitude,
			String category) {

		String responseEntity = ClientBuilder.newClient()
				.target("https://api.foursquare.com").path("v2/venues/search")
				.queryParam("v", "20131016")
				.queryParam("ll", latitude + "," + longitude)
				.queryParam("venuePhotos", "1")
				.queryParam("categoryId", categoryMap.get(category))
				.queryParam("oauth_token", auth)
				.request()
				.get(String.class);


		return parseRecommendedPlacesWithPhoto(responseEntity);

	}

	private String getVenuePhoto(String venueID) {


		String path = "v2/venues/" + venueID + "/photos";

		String responseEntity = ClientBuilder.newClient()
				.target("https://api.foursquare.com").path(path)
				.queryParam("v", "20131016").queryParam("limit", 1)
				.queryParam("oauth_token", auth).request()
				.get(String.class);

		return parsePhotoResponse(responseEntity);

	}


}
