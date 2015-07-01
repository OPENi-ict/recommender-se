package control.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



import control.places.train.Training;

public class WeightTraining {

	private static String[] interests = {"books","cars","shopping","games","fashion", "events", 
		"fitness", "travel",  "outdoors", "museums", "galleries and centers"   , "architecture",
		"fish tank","travelling","ps3","football","cinema","tv","jogging","3d modeling","piano","guitar",
		"art","writing","movies","engineering","sports","rural electrification","politics","history",
		"mountaineering","browsing","computer games","reading","tennis","programming","martial arts",
		"basketball","hanging out","hiking","camping","baseball","rugby","surfing","mac","computers","gym",
		"dancing","business","startup","swimming","eating","soccer","music","literature","cooking",
		"photography","yoga","dance","theater","tv series","working","fishing","pets"};

	private static String[] educationLevels = {
		"primary",
		"secondary",
		"college",
		"bachelor",
		"masters",
		"doctorate"
	};

	private static String[] ageGroups = {
		"13-17",
		"18-24",
		"25-34",
		"35-44",
		"45-54",
		"55-64",
		"65+"
	};

	private static String[] gender = {
		"male",
		"female"
	};

	private static String SERVER_ROOT_URI;


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

	private static void createAgeWeights (){
		
//		double total = 2262.0;
		double total_age = 1634.0;
		
		for (int i=0;i<ageGroups.length;i++){

			try{
				String payload = "{\"statements\" : [ {\"statement\" : \" match (n:Age {value:\\\""+ageGroups[i]+"\\\"})-[r:HASCONTEXT]-(:Person) with n,count(r) as cnt set n.weight=round((10.0*"+total_age+")/cnt)/10.0 \"} ]}";
				System.out.println(payload);
				Client client = ClientBuilder.newClient();
				WebTarget target = client.target(SERVER_ROOT_URI);
				Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);
				String res = (response.readEntity(String.class));
				System.out.println(res);

			}
			catch (Exception e){
				e.printStackTrace();
			}
		}

	}
	
private static void createGenderWeights (){
		
//		double total = 2262.0;
		double total_age = 1634.0;
		
		for (int i=0;i<gender.length;i++){

			try{
				String payload = "{\"statements\" : [ {\"statement\" : \" match (n:Gender {value:\\\""+gender[i]+"\\\"})-[r:HASCONTEXT]-(:Person) with n,count(r) as cnt set n.weight=round((10.0*"+total_age+")/cnt)/10.0 \"} ]}";
				System.out.println(payload);
				Client client = ClientBuilder.newClient();
				WebTarget target = client.target(SERVER_ROOT_URI);
				Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);
				String res = (response.readEntity(String.class));
				System.out.println(res);

			}
			catch (Exception e){
				e.printStackTrace();
			}
		}

	}

private static void createEducationWeights (){
	
//	double total = 2262.0;
	double total_age = 1634.0;
	
	for (int i=0;i<educationLevels.length;i++){

		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" match (n:Education {value:\\\""+educationLevels[i]+"\\\"})-[r:HASCONTEXT]-(:Person) with n,count(r) as cnt set n.weight=round((10.0*"+total_age+")/cnt)/10.0 \"} ]}";
			System.out.println(payload);
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);
			String res = (response.readEntity(String.class));
			System.out.println(res);

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}

private static void createInterestsWeights (){
	
//	double total = 2262.0;
	double total_age = 1634.0;
	
	for (int i=0;i<interests.length;i++){

		try{
			String payload = "{\"statements\" : [ {\"statement\" : \" match (n:Interests {value:\\\""+interests[i]+"\\\"})-[r:HASCONTEXT]-(:Person) with n,count(r) as cnt set n.weight=round((10.0*"+total_age+")/cnt)/10.0 \"} ]}";
			System.out.println(payload);
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SERVER_ROOT_URI);
			Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(payload, MediaType.APPLICATION_JSON), Response.class);
			String res = (response.readEntity(String.class));
			System.out.println(res);

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}
	
	public static void main(String[] args){
		
		createAgeWeights();
		createInterestsWeights();
		createEducationWeights();
		createGenderWeights();
	}
}
