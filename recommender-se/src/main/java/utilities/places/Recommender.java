package utilities.places;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.ContextPermissions;
import models.Person;
import control.places.checkins.CheckinUtilities;

public class Recommender {

	private static String SERVER_ROOT_URI;


	static{
		Properties props = new Properties();
		InputStream in = Recommender.class.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			SERVER_ROOT_URI = props.getProperty("SERVER_ROOT_URI");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getCategoryFromShortestPaths(String auth, ContextPermissions permissions, String timestamp){


		String pid = createRecoNodeAndContext(auth, permissions, timestamp);
		String category = getRecommendedCategory(pid);
		System.out.println(category);
		return category;

	}

	private static String createRecommendationNode(){


		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" create (n:Recommendation:PlaceRecom {timestamp:timestamp()}) return id(n) \"} ]}";

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

			String res = (response.readEntity(String.class));

			System.out.println(res);
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
			JsonArray results = jobj.get("results").getAsJsonArray();
			JsonObject jo = results.get(0).getAsJsonObject();
			JsonArray data = jo.get("data").getAsJsonArray();
			//			
			JsonObject je = data.get(0).getAsJsonObject();
			String id = je.get("row").getAsString();

			System.out.println(id);
			return id;

		}
		catch (Exception e){
			e.printStackTrace();
			return "0";
		}

	}

	private static String createRecoNodeAndContext(String auth,ContextPermissions permissions, String timestamp){

		String pid = createRecommendationNode();
		Person p;
		System.out.println("auth:"+auth);
		if (auth.equals("1")){
			p = new Person("/user_1.properties");
		}
		else if (auth.equals("2")){
			p = new Person("/user_2.properties");
		}
		else{
			p = new Person("/user_3.properties");

		}
		String payload="{\"statements\" : [ {\"statement\" : \" match ";
		String match = 	"(n:Recommendation) ";
		String where = " where id(n)= "+pid;
		String create = "";

		if (permissions.isUseGender()){
			match+=", (gender:Gender {value:\\\""+p.getGender()+"\\\"})";
			create+=" create unique (n)-[rg:HASCONTEXT]->(gender) ";
		}
		if (permissions.isUseAge()){
			match+=", (age:Age {value:\\\""+p.getAge()+"\\\"})";
			create+=" create unique (n)-[ra:HASCONTEXT]->(age) ";
		}
		if (permissions.isUseChildrenNumber()){
			if((p.getChildrenNum()).equals("0")){
				match+=", (num:HasChildren {value:false})";
			}
			else{
				match+=", (num:HasChildren {value:true,number:"+p.getChildrenNum()+"})";
			}
			create+=" create unique (n)-[rh:HASCONTEXT]->(num) ";
		}
		if (permissions.isUseEducation()){
			match+=", (edu:Education {value:\\\""+p.getEducation()+"\\\"})";
			create+=" create unique (n)-[re:HASCONTEXT]->(edu) ";
		}
		if (permissions.isUseMarriageState()){
			if ((p.getMarried()).equals("false")){
				match+=", (mar:IsMarried {value:false})";
			}
			else {
				match+=", (mar:IsMarried {value:true})";
			}
			create+=" create unique (n)-[ri:HASCONTEXT]->(mar)";
		}
		if (permissions.isUseTime()){
			try{
				Long tms = Long.parseLong(timestamp);
				boolean isWeekday = CheckinUtilities.isWeekday(tms);
				int weekCode = CheckinUtilities.getDayCode(tms);
				match+=", (mw:Context:WeekDay {value:"+weekCode+",isWeekday:"+isWeekday+"})";
				create+=" create unique (n)-[rmw:HASCONTEXT]->(mw)";
			}
			catch (NumberFormatException e){ //wrong timestamp format should be handled in service call, you should never reach this error
				//TODO handle it with error response?
			}
		}
		if (permissions.isUseInterests()){
			String[] interests = p.getInterests();
			for(int i=0;i<interests.length;i++){
				match+=", (int"+i+":Interests {value:\\\""+interests[i]+"\\\"})";
				create+=" create unique (n)-[rint"+i+":HASCONTEXT]->(int"+i+")";
			}
		}		

		payload += match +where+ create+" \"} ]}";
		System.out.println(payload);
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_ROOT_URI);
		target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		return pid;
	}

	private static String getRecommendedCategory(String pid){
		String payload = "{\"statements\" : [ {\"statement\" : \" MATCH p = allShortestPaths(source-[r*..3]-(destination:PlaceType)) where id(source)="+pid+
				" RETURN destination.name as name,count(p) as total order by total desc limit 5 \"} ]}";
		System.out.println(payload);

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_ROOT_URI);
		Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		try{
			String res = (response.readEntity(String.class));

			System.out.println(res);
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
			JsonArray results = jobj.get("results").getAsJsonArray();
			JsonObject jo = results.get(0).getAsJsonObject();
			JsonArray data = jo.get("data").getAsJsonArray();
			JsonObject je = data.get(0).getAsJsonObject();
			String name = (je.get("row").getAsJsonArray()).get(0).getAsString();
			return name;
		}catch (Exception e){
			e.printStackTrace();
			return "Park";
		}
	}



}
