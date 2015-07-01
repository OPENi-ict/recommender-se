package control.places.train;

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
	private static String DB_INITIAL_DATA_LOCATION;
	private static HashMap<String,String> persons; //person id from sqlite3 to person chars


	static{
		Properties props = new Properties();
		InputStream in = Training.class.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			SERVER_ROOT_URI = props.getProperty("SERVER_ROOT_URI");
			DB_INITIAL_DATA_LOCATION = props.getProperty("DB_INITIAL_DATA_LOCATION");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void parsePersons(){

		persons = new HashMap<String, String>();

		try {
			//id,fsq_user_id,gender,educationalLevel,birthday,children,married,income,interests,country,ethnicity
			BufferedReader in = new BufferedReader(new FileReader(new File(DB_INITIAL_DATA_LOCATION+"mysqlPeople.csv")));
			String line;
			String[] contents;
			while ((line = in.readLine()) != null) {
				contents = line.split(",");
				persons.put(contents[0], line);

			}
			in.close();
			for (String k:persons.keySet())
				System.out.println(k+"   "+persons.get(k));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private static String createPerson(long timestamp){

		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" create (n:Person{timestamp:"+timestamp+"}) return id(n) \"} ]}";

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

			String res = (response.readEntity(String.class));
			JsonObject jobj = new Gson().fromJson(res, JsonObject.class);
			JsonArray results = jobj.get("results").getAsJsonArray();
			JsonObject jo = results.get(0).getAsJsonObject();
			JsonArray data = jo.get("data").getAsJsonArray();
			//			
			JsonObject je = data.get(0).getAsJsonObject();
			String id = je.get("row").getAsString();

			return id;

		}
		catch (Exception e){
			e.printStackTrace();
			return "0";
		}

	}

	private static String createPersonWithContext(String personListId, long timestamp){

		String pid = createPerson(timestamp);
		String line = persons.get(personListId);
		String[] contents = line.split(",");
		

		String payload="{\"statements\" : [ {\"statement\" : \" match ";
		String match = 	"(n:Person) ";
		String where = " where id(n)= "+pid;
		String create = "";
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
			String birthyear = birthday.substring(0, birthday.indexOf('-'));
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
		//		String income = contents[7];
		String interest = contents[8];
		interest = interest.replace("\"", "");
		String interestsCorrect = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));

		String[] interests = interestsCorrect.split(",");
		for(int i=0;i<interests.length;i++){
			match+=", (int"+i+":Interests {value:\\\""+interests[i]+"\\\"})";
			create+=" create unique (n)-[rint"+i+":HASCONTEXT]->(int"+i+")";
		}

		payload += match +where+ create+" \"} ]}";
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SERVER_ROOT_URI);
		target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		return pid;
	}

	private static void createTraining(String pid, String category){

		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" match (n:Person),(m:PlaceCategory {name:\\\""+category+"\\\"}) where id(n)= "+pid
					+ " CREATE UNIQUE (n)-[r:CHECKIN]->(m) "
					+ " \"} ]}";
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);

		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	private static void parseCheckins(){


		try {
			//user_id,category_name,timestamp
			BufferedReader in = new BufferedReader(new FileReader(new File(DB_INITIAL_DATA_LOCATION+"mysqlCheckins.csv")));
			String line;
			String[] contents;

			while ((line = in.readLine()) != null) {

				contents = line.split(",");
				String pid = createPersonWithContext(contents[0],Long.parseLong(contents[2]));
				createTraining(pid, contents[1]);

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

	private static void train(){

		parsePersons();
		parseCheckins();

	}

	public static void main(String[] args){

		train();
	}

}
