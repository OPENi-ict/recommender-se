package utilities.products;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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





//import control.places.checkins.CheckinInitializer;
import models.ContextPermissions;
import models.Person;
import models.ProductRecommendation;
import models.ProductRecommendationCollection;

public class ProductRecommender {

	private static String SERVER_ROOT_URI;


	static{
		Properties props = new Properties();
		InputStream in = ProductRecommender.class.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			SERVER_ROOT_URI = props.getProperty("SERVER_ROOT_URI");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static String getRecommendation(String authorization,ContextPermissions permissions, String sortBy){

		System.out.println(authorization);
		String pid = createRecoNodeAndContext(authorization, permissions);
		ProductRecommendationCollection collection = getProducts(pid,sortBy);
		return collection.getCollectionAsJsonObject();
	}

	public static String getRecommendation(String authorization,ContextPermissions permissions, String sortBy, String category){

		if(category.equals("All"))
			getRecommendation(authorization, permissions, sortBy);
		System.out.println(authorization);
		String pid = createRecoNodeAndContext(authorization, permissions);
		ProductRecommendationCollection collection = getProducts(pid,sortBy,category);
		return collection.getCollectionAsJsonObject();
	}

	private static String createRecommendationNode(){


		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" create (n:Recommendation:ProductRecom {timestamp:timestamp()}) return id(n) \"} ]}";

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

	private static String createRecoNodeAndContext(String auth,ContextPermissions permissions){

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
		Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		String res = (response.readEntity(String.class));

		System.out.println(res);
		return pid;
	}

	private static ProductRecommendationCollection parseProductRecommendationResponse(String res, int scoreFieldId){

		ArrayList<ProductRecommendation> list = new ArrayList<ProductRecommendation>();
		JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
		JsonArray results = jobj.get("results").getAsJsonArray();
		JsonObject jo = results.get(0).getAsJsonObject();
		JsonArray data = jo.get("data").getAsJsonArray();

		for (int i=0;i<data.size();i++){
			JsonObject je = data.get(i).getAsJsonObject();
			String name = (je.get("row").getAsJsonArray()).get(0).getAsString();
			String code = (je.get("row").getAsJsonArray()).get(1).getAsString();
			String image = (je.get("row").getAsJsonArray()).get(2).getAsString();
			String[] cats = new String[1];
			cats[0]= (je.get("row").getAsJsonArray()).get(3).getAsString();
			String score = (je.get("row").getAsJsonArray()).get(scoreFieldId).getAsString();
			ProductRecommendation p = new ProductRecommendation(name, "N/A", image, Double.parseDouble(score), cats, code);
			list.add(p);
		}

		ProductRecommendation[] array = new ProductRecommendation[list.size()];
		array = list.toArray(array);

		ProductRecommendationCollection collection = new ProductRecommendationCollection(array);
		return collection;
		//		System.out.println(object);

	}

	private static ProductRecommendationCollection getProducts(String pid, String sortBy){

		String payload = "{\"statements\" : [ {\"statement\" : \" MATCH "
				+ "(n:ProductRecom)-[hase]-(:Context)-[]-(:Person)-[]-"
				+ "(:Training)-[include]-(pr:Product) where id(n)="+pid
				+ " RETURN pr.product_name,pr.product_code,pr.image,pr.category_name, "
				+ "count(include) as count,"
				+ "sum(include.rating) as sum,"
				+ "sum(include.rating)/count(include) as mean "
				+ "order by "+sortBy
				+ " desc  LIMIT 20 \"} ]}";
		System.out.println(payload);

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_ROOT_URI);
		Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		String res = (response.readEntity(String.class));
		int sortFieldId = 6;
		if(sortBy.equals("count")){
			sortFieldId = 4;
		}
		else if (sortBy.equals("sum")){
			sortFieldId = 5;
		}

		return parseProductRecommendationResponse(res, sortFieldId);		

	}

	private static ProductRecommendationCollection getProducts(String pid, String sortBy, String category){

		category = category.replace("\"", "\\\"");
		String payload = "{\"statements\" : [ {\"statement\" : \" MATCH "
				+ "(n:ProductRecom)-[hase]-(:Context)-[]-(:Person)-[]-"
				+ "(:Training)-[include]-(pr:Product) where id(n)="+pid
				+ " and pr.category_name in ["+category
				+ "] RETURN pr.product_name,pr.product_code,pr.image,pr.category_name, "
				+ "count(include) as count,"
				+ "sum(include.rating) as sum,"
				+ "sum(include.rating)/count(include) as mean "
				+ "order by "+sortBy
				+ " desc  LIMIT 20 \"} ]}";
		System.out.println(payload);
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_ROOT_URI);
		Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		String res = (response.readEntity(String.class));
		int sortFieldId = 6;
		if(sortBy.equals("count")){
			sortFieldId = 4;
		}
		else if (sortBy.equals("sum")){
			sortFieldId = 5;
		}

		return parseProductRecommendationResponse(res, sortFieldId);		

	}

}
