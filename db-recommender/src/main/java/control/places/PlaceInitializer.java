package control.places;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import control.places.train.Training;
import dbtransactions.DatabaseUtils;
import dbtransactions.DatabaseUtils.LabelTypes;
import dbtransactions.DatabaseUtils.RelTypes;

public class PlaceInitializer {
	
static String DB_INITIAL_DATA_LOCATION;
	
	static{
		Properties props = new Properties();
		InputStream in = Training.class.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			DB_INITIAL_DATA_LOCATION = props.getProperty("DB_INITIAL_DATA_LOCATION");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void initialize(String db_path) throws URISyntaxException, IOException{
		
		DatabaseUtils dbr = new DatabaseUtils(db_path);
		System.out.println("*** Starting place categories import... ***");
		createPlaceCategoryNodesFromFile(dbr);
		System.out.println("*** Place categories imported. ***");
		System.out.println("*** Starting place subcategory relationships creation... ***");
		createPlaceCategoryRelationships(dbr);
		System.out.println("*** Place subcategory relationships created. ***");
		dbr.shutDownDB();
		
	}
	
	
	/**
	 * read place category names from csv file
	 * for every top level category create a topPlaceType node, for every other category create a PlaceType node
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	private static void createPlaceCategoryNodesFromFile(DatabaseUtils dbr) throws URISyntaxException, IOException{
		
		
		String provenance = "frsq";
		String code;
		int count = 0;
		BufferedReader in = new BufferedReader(new FileReader(new File(DB_INITIAL_DATA_LOCATION+"FoursquarePlaces.csv")));

		String line;
			

			while ((line = in.readLine()) != null) {
				count++;
				code = provenance + "_" + count;
				String[] contents = line.split(",");
				String nodeName = contents[contents.length-1];
				if (contents.length==1){
					PlaceCategory pc = new PlaceCategory(nodeName, provenance, code, LabelTypes.TopPlaceType);
					dbr.createPlaceType(pc);
				}
				else{
					PlaceCategory pc = new PlaceCategory(nodeName, provenance, code, LabelTypes.PlaceType);
					dbr.createPlaceType(pc);
				}
			}
			in.close();
	}
	
	/**
	 * read place category names from  csv file
	 * create a relationship for every parent-child nodes pair
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */	
	private static void createPlaceCategoryRelationships(DatabaseUtils dbr) throws URISyntaxException, IOException {
		
		
		String[] pinax = new String[5];
		
		BufferedReader in = new BufferedReader(new FileReader(new File(DB_INITIAL_DATA_LOCATION+"FoursquarePlaces.csv")));

		String line;

		while ((line = in.readLine()) != null) {
			String[] contents = line.split(",");
			int depth = contents.length;
			pinax[depth-1] = contents[contents.length-1];
			if(depth>1)
				dbr.createPlaceSubcategoryRelationship(pinax[depth-2],pinax[depth-1], RelTypes.SUBCATEGORY);
			
			
		}
		in.close();
	}
	
	

}
