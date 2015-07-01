package utilities.training;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import utilities.ContextRetriever;
import utilities.places.Recommender;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import control.products.Product;

public class SystemTraining {
	
	private static String OPENI_FRAMEWORK_URI;
	
	static{
		Properties props = new Properties();
		InputStream in = Recommender.class.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			OPENI_FRAMEWORK_URI = props.getProperty("OPENI_FRAMEWORK_URI");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean train(String auth){
		if (ContextRetriever.getContext(auth)!=null)
			if (getApplications(auth)&getCheckins(auth)&getProducts(auth))
				return true;
		return false;
	}
	
	public static boolean getCheckins(String auth){
		
		try{

			String productsURL = OPENI_FRAMEWORK_URI + "/Checkin/";
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(productsURL);
			Response response = target.request().header("Authorization", auth).get();

			String res = response.readEntity(String.class);
			System.out.println(res);
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
//			JsonArray cbsResults = jobj.get("cbs").getAsJsonArray();
			JsonObject cloudletResults = jobj.get("cloudlet").getAsJsonObject();
			JsonArray cloudletCheckins = cloudletResults.get("objects").getAsJsonArray();
			System.out.println("Number of checkins retrieved: "+cloudletCheckins.size());
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean getProducts(String auth){
		try{

			String code;
			String category;
			String productsURL = OPENI_FRAMEWORK_URI + "/Product/";
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(productsURL);
			Response response = target.request().header("Authorization", auth).get();

			String res = response.readEntity(String.class);
			System.out.println(res);
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
			//no products from cbs, so only cloudlet applies
			JsonObject cloudletResults = jobj.get("cloudlet").getAsJsonObject();
			JsonArray cloudletProducts = cloudletResults.get("objects").getAsJsonArray();
			System.out.println("Number of retrieved checkins: "+cloudletProducts.size());
			ArrayList<Product> products = new ArrayList<Product>();
			
			for (int i=0;i<cloudletProducts.size();i++){
				JsonObject productJObject = cloudletProducts.get(i).getAsJsonObject();
				if (productJObject.has("code")){
					if (!(productJObject.get("code")).isJsonNull())
						code = productJObject.get("code").getAsString();
					else
						code = "N/A";
				}
					
				else
					code = "N/A";
				if (productJObject.has("category")){
					if (!(productJObject.get("category")).isJsonNull())
						category = productJObject.get("category").getAsString();
					else
						category = "N/A";
				}
					
				else
					category = "N/A";
				Product p = new Product("test", code , "", "", "", "", "", "", "", "", "", "", category);
				products.add(p);
				
			}
			return true;


		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean getApplications(String auth){
		return true;
	}
	
	
	
	public static void main(String[] args){
		
		String auth = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJjX2U3ODQ0NjM2MWIxMjhiNTIzYzAyMjU4MjAyNzc4YTBiX2NjODFkOWU3LThmZGQtNDk5Ny1hNDRkLWMzMGUwMTM1OTUwNyIsImlzcyI6Imh0dHBzOi8vMTI3LjAuMC4xL2F1dGgvdG9rZW4iLCJzdWIiOiJjX2U3ODQ0NjM2MWIxMjhiNTIzYzAyMjU4MjAyNzc4YTBiIiwiZXhwIjoxNDM1NDQ0Njc4LCJpYXQiOjE0MzU0MDE0NzgsIm5vbmNlIjoiNTEwMzkxY2EtMGM2ZC00ZjU4LTk0MDQtMjZlNzI2ZDJkYWQ5IiwidXNlcl9pZCI6ImNfZTc4NDQ2MzYxYjEyOGI1MjNjMDIyNTgyMDI3NzhhMGIiLCJjbG91ZGxldCI6ImNfZTc4NDQ2MzYxYjEyOGI1MjNjMDIyNTgyMDI3NzhhMGIiLCJjbGllbnRfaWQiOiJjZTdmMzFiYzU1ZGNhNzE4M2ZhZWU5OTc1MmIzMWJlZCIsImNsaWVudF9uYW1lIjoidGVzdGMiLCJjb250ZXh0IjoiY183ZTc4YjgzZGRhN2E0N2JjZTM5MjFlYjcyZTk1YjMyOSIsInNjb3BlIjoib3BlbmkiLCJvcGVuaS10b2tlbi10eXBlIjoidG9rZW4iLCJyZXNwb25zZV90eXBlIjoiaWRfdG9rZW4ifQ.Tdte_GfcynNflvjHVdq9ivUrqZyf9E5J4NuiTWxjB8AroJdAoHL08BxHmLFhFz3tBDALeIH7T9WKCLjMackjvg";
		System.out.println(train(auth));
		
	}

}
