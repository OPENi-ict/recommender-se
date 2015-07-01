package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import utilities.places.Recommender;
import models.Person;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ContextRetriever {
	
	private static String OPENI_SEARCH_URI;


	static{
		Properties props = new Properties();
		InputStream in = Recommender.class.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			OPENI_SEARCH_URI = props.getProperty("OPENI_SEARCH_URI");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static Person getContext(String auth){
		
		Person p = null;
		
		String contextURL = OPENI_SEARCH_URI + "with_property=cbsid&property_filter=recommender&type=t_0b26774118f2346c002dd2aa26e04b7a-893";
		try{

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(contextURL);
			Response response = target.request().header("Authorization", auth).get();
			String[] interests={};
			String gender="";
			String age="";
			String education="";
			boolean hasPermissions = false;

			String res = response.readEntity(String.class);
			System.out.println(res);
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
			JsonArray results = jobj.get("result").getAsJsonArray();
			JsonObject result = results.get(results.size()-1).getAsJsonObject();
			JsonObject data = result.get("@data").getAsJsonObject();
			JsonObject context = data.get("context").getAsJsonObject();
			if (context.has("personalization_interests"))
				if (!(context.get("personalization_interests")).isJsonNull()){
					interests = context.get("personalization_interests").getAsString().split(",");
					hasPermissions = true;
					}
			if (context.has("personalization_gender"))
				if (!(context.get("personalization_gender")).isJsonNull()){
					gender = context.get("personalization_gender").getAsString();
					hasPermissions = true;
					}
			if (context.has("personalization_education"))
				if (!(context.get("personalization_education")).isJsonNull()){
					education = context.get("personalization_education").getAsString();
					hasPermissions = true;
					}
			if (context.has("personalization_age_range"))
				if (!(context.get("personalization_age_range")).isJsonNull()){
					age = context.get("personalization_age_range").getAsString();
					hasPermissions = true;
					}
			
				
			if (!hasPermissions)
				return p;
			p = new Person(gender, age, education, interests);
			System.out.println("Context: "+context+" is used to create this Person \n Age: "+p.getAge()+"\n Education: "+p.getEducation()+"\n Gender: "+p.getGender()+"\n Number of Interests: "+p.getInterests().length);
			
			

		}
		catch (Exception e){
			e.printStackTrace();
		}
		return p;
	}

}
