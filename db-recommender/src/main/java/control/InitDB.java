package control;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import control.context.Context;
import control.places.PlaceInitializer;
import control.products.ProductBatchInitializer;


public class InitDB {
	
	private static String DB_PATH;
	
	static{
		Properties props = new Properties();
		InputStream in = InitDB.class.getResourceAsStream("/props.properties");
		
		try {
			props.load(in);
			DB_PATH = props.getProperty("DB_PATH");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void placesInitialize() throws URISyntaxException, IOException{
		
		PlaceInitializer.initialize(DB_PATH);
	}
	
    private static void productsInitialize() throws IOException, URISyntaxException{
		
		ProductBatchInitializer.initialize();
	}
    
    
    private static void contextInitialize(){
    	Context.initialize();
    }
	
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		placesInitialize();
		productsInitialize();
		contextInitialize();



	}

}
