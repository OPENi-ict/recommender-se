package control.products.train;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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

public class Training {
	
	private static String SERVER_ROOT_URI;
	private static HashMap<String,String> persons; //person id from sqlite3 to training id from neo4j
	
	
	static{
		Properties props = new Properties();
		InputStream in = Training.class.getResourceAsStream("/props.properties");
		
		try {
			props.load(in);
			SERVER_ROOT_URI = props.getProperty("SERVER_ROOT_URI");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void train(){
		
		parsePersons();
		parseProductsToRating();
		
	}
	
	
	private static void parsePersons(){
		
		persons = new HashMap<String, String>();
		
		try {
			//id,fsq_user_id,gender,educationalLevel,birthday,children,married,income,interests,country,ethnicity
			BufferedReader in = new BufferedReader(new FileReader(new File("C:\\Users\\Evmorfia\\Documents\\openi_recommender_training\\newUsers.csv")));
			String line;
			String[] contents;
			while ((line = in.readLine()) != null) {
				String pid = createPerson();
				String payload="{\"statements\" : [ {\"statement\" : \" match ";
				String match = 	"(n:Person) ";
				String where = " where id(n)= "+pid;
				String create = "";
				contents = line.split(",");
				String gender = contents[2];
				if(gender.length()>0){
					if (gender.equals("F")) gender="female";
					else gender="male";
					match+=", (gender:Gender {value:\\\""+gender+"\\\"})";
					create+=" create unique (n)-[rg:HASCONTEXT]->(gender) ";
				}
				String education = contents[3];
				if(education.length()>0){
					match+=", (edu:Education {value:\\\""+education+"\\\"})";
					create+=" create unique (n)-[re:HASCONTEXT]->(edu) ";
				}
				String birthday = contents[4];
				if(birthday.length()>0){
					System.out.println(birthday);
					String birthyear = birthday.substring(0, birthday.indexOf('-'));
					System.out.println(birthyear);
					int age = 2014 - Integer.parseInt(birthyear);
					String ageGroup;
					if (age<18){
						ageGroup = "13-17";
					}
					else if (age<25){
						ageGroup = "18-24";
					}
	                else if (age<35){
						ageGroup = "25-34";
					}
	                else if (age<45){
						ageGroup = "35-44";
					}
	                else if (age<55){
						ageGroup = "45-54";
					}else if (age<65){
						ageGroup = "54-65";
					}
					else{
						ageGroup = "65+";
					}
					match+=", (age:Age {value:\\\""+ageGroup+"\\\"})";
					create+=" create unique (n)-[ra:HASCONTEXT]->(age) ";
				}
				
				String numberOfChildren = contents[5];
				if(numberOfChildren.length()>0){
					if(numberOfChildren.equals("0")){
					    match+=", (num:HasChildren {value:false})";
					}
					else{
						match+=", (num:HasChildren {value:true,number:"+numberOfChildren+"})";
					}
					create+=" create unique (n)-[rh:HASCONTEXT]->(num) ";
				}
				String married = contents[6];
				if(married.length()>0){
					
					if (married.equals("0")){
						match+=", (mar:IsMarried {value:false})";
					}
					else {
						match+=", (mar:IsMarried {value:true})";
					}
					create+=" create unique (n)-[ri:HASCONTEXT]->(mar)";
	                
				}
//				String income = contents[7];
				String interest = contents[8];
				interest = interest.replace("\"", "");
				String[] interests = interest.split(",");
				for(int i=0;i<interests.length;i++){
					match+=", (int"+i+":Interests {value:\\\""+interests[i]+"\\\"})";
					create+=" create unique (n)-[rint"+i+":HASCONTEXT]->(int"+i+")";
				}
				
				payload += match +where+ create+" \"} ]}";
				System.out.println(payload);
				Client client = ClientBuilder.newClient();
				WebTarget target = client.target(SERVER_ROOT_URI);
		        Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

				String res = (response.readEntity(String.class));
				
				System.out.println(res);
				persons.put(contents[0], pid);
//				break;

			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private static String createPerson(){
		
		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" create (n:Person{timestamp:timestamp()}) return id(n) \"} ]}";

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
	
	
	private static String createTraining(String pid){
		
		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" match (n:Person) where id(n)= "+pid
					+ " CREATE UNIQUE (n)-[r:GIVES]->(m:Training { timestamp:timestamp() }) "
					+ "return id(m) \"} ]}";
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			System.out.println(payload);

	        Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

			String res = (response.readEntity(String.class));
			
			System.out.println(res);
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
			JsonArray results = jobj.get("results").getAsJsonArray();
			JsonObject jo = results.get(0).getAsJsonObject();
			JsonArray data = jo.get("data").getAsJsonArray();
//			
			JsonObject je = data.get(0).getAsJsonObject();
			String tid = je.get("row").getAsString();
			
			System.out.println(tid);
			
			
			return tid;
			
	    }
	   	catch (Exception e){
	   		e.printStackTrace();
	   		return "0";
	    }
		
	}
	
	private static void parseProductsToRating(){
		String payload="";
		String match = 	"";
		String where = "";
		String create = "";
		int count = 0;
		
		try {
			//id,product_id,rate,createdBy_id
			BufferedReader in = new BufferedReader(new FileReader(new File("C:\\Users\\Evmorfia\\Documents\\openi_recommender_training\\newUsersWithDataOrdered.csv")));
			String line;
			String[] contents;
			String pid="";
			String current;
			String rid="";
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			while ((line = in.readLine()) != null) {

				count++;
				contents = line.split(",");
				current = contents[contents.length-1];
				System.out.println(count+"   "+pid+"    "+current);

				if(!current.equals(pid)){
					
					pid = current;	
			        rid = createTraining(persons.get(pid));
					
					
					
				}
				
					
					payload="{\"statements\" : [ {\"statement\" : \" match ";
					match = 	"(n:Training),(p1:Product {product_code:\\\""+contents[1]+"\\\"}) ";
					where = " where id(n)= "+rid;
					create = "create unique (n)-[r:INCLUDES {rating:"+contents[contents.length-2]+"}]->(p1)";
					payload += match +where+ create+" \"} ]}";
			        Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);
			        String res = (response.readEntity(String.class));
					System.out.println(res);
								
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
 
	public static void main(String[] args){

		train();
	}
}
