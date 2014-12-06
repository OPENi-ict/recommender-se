package control.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import dbtransactions.DatabaseUtils;

public class Context {
	
    private static String DB_PATH;
	
	static{
		Properties props = new Properties();
		InputStream in = Context.class.getResourceAsStream("/props.properties");
		
		try {
			props.load(in);
			DB_PATH = props.getProperty("DB_PATH");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String[] ageGroupQueries = {
		"MERGE (n:Context:Demographics:Age {value:\"55-64\",upper:64,lower:55})",
		"MERGE (n:Context:Demographics:Age {value:\"65+\",lower:55})",
		"MERGE (n:Context:Demographics:Age {value:\"45-54\",upper:54,lower:45})",
		"MERGE (n:Context:Demographics:Age {value:\"35-44\",upper:44,lower:35})",
		"MERGE (n:Context:Demographics:Age {value:\"25-34\",upper:34,lower:25})",
		"MERGE (n:Context:Demographics:Age {value:\"18-24\",upper:24,lower:18})",
		"MERGE (n:Context:Demographics:Age {value:\"13-17\",upper:17,lower:13})"
		
	};
	
	private static String[] childrenQueries = {
		"MERGE (n:Context:Personal:HasChildren {value:true,number:1})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:2})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:3})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:4})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:5})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:6})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:7})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:8})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:9})",
		"MERGE (n:Context:Personal:HasChildren {value:true,number:10})",
		"MERGE (n:Context:Personal:HasChildren {value:false})",
		"MERGE (n:Context:Personal:IsMarried {value:false})",
		"MERGE (n:Context:Personal:IsMarried {value:true})"
	};
	
	private static String[] moodQueries = {
		"MERGE (n:Context:Personal:Mood {value:\"happy\"})",
        "MERGE (n:Context:Personal:Mood {value:\"sad\"})",
        "MERGE (n:Context:Personal:Mood {value:\"bored\"})",
        "MERGE (n:Context:Personal:Mood {value:\"tired\"})",
        "MERGE (n:Context:Personal:Mood {value:\"angry\"})"
	};
	
	private static String[] interests = {"books","cars","shopping","games","fashion", "events", 
	"fitness", "travel",  "outdoors", "museums", "galleries and centers"   , "architecture",
	"fish tank","travelling","ps3","football","cinema","tv","jogging","3d modeling","piano","guitar",
	"art","writing","movies","engineering","sports","rural electrification","politics","history",
	"mountaineering","browsing","computer games","reading","tennis","programming","martial arts",
	"basketball","hanging out","hiking","camping","baseball","rugby","surfing","mac","computers","gym"};

	private static String[] genderQueries = {
		"MERGE (n:Context:Demographics:Gender {value:\"female\"})",
		"MERGE (n:Context:Demographics:Gender {value:\"male\"})"
    };
	
	private static String[] educationLevels = {
		"primary",
		"secondary",
		"college",
		"bachelor",
		"masters",
		"doctorate"
	};
	
	public static void initialize(){
		
		System.out.println("*** Initializing context nodes. ***");
		
		DatabaseUtils dbr = new DatabaseUtils(DB_PATH);
		try{
		    initializeAgeGroups(dbr);
		    initializeGender(dbr);
		    initializeChildren(dbr);
		    initializeCountries(dbr);
		    initializeEducation(dbr);
		    initializeMood(dbr);
		    initializeInterests(dbr);
		}
		finally{
			dbr.shutDownDB();
		}
		System.out.println("*** Initialized context nodes. ***");
		
		
	}
	
	private static void initializeAgeGroups(DatabaseUtils dbr){
		
		for(String q:ageGroupQueries)
			dbr.executeQuery(q);
		
	}
	
    private static void initializeGender(DatabaseUtils dbr){
    	
    	for(String q:genderQueries)
			dbr.executeQuery(q);
		
	}
    
    private static void initializeInterests(DatabaseUtils dbr){
    	for (String interest:interests){
    		String query = "MERGE (n:Context:Preferences:Interests {value:\""+interest+"\"})";
    		dbr.executeQuery(query);
    	}
	}
    
    private static void initializeMood(DatabaseUtils dbr){
    	
    	for(String q:moodQueries)
			dbr.executeQuery(q);
		
	}
    
    private static void initializeEducation(DatabaseUtils dbr){
    	
    	for (String level:educationLevels){
    		String query = "MERGE (n:Context:Demographics:Education {value:\""+level+"\"})";
    		dbr.executeQuery(query);
    	}
		
	}
    
    private static void initializeChildren(DatabaseUtils dbr){
    	
    	for(String q:childrenQueries)
			dbr.executeQuery(q);
		
	}
    
    
    private static void initializeCountries(DatabaseUtils dbr){
    	
    	//TODO
		
	}
 
}
