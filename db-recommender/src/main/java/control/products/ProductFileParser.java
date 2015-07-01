package control.products;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import control.places.train.Training;


public class ProductFileParser {
	
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

	//hashmap: [category name]-->[category code]
	static ArrayList<ProductCategory> getProductCategories() throws IOException{

		BufferedReader in = new BufferedReader(new FileReader(new File(DB_INITIAL_DATA_LOCATION+"productsCleanedWithCategoriesAndImageSelectedFieldsPlusBrand.csv")));

		String line;
		String[] lineContents;
		ArrayList<ProductCategory> categoryList = new ArrayList<ProductCategory>();
		ProductCategory pcategory;
		HashMap<String,String> categories = new HashMap<String,String>();
		while ((line = in.readLine()) != null) {

			lineContents = line.split(";");
			categories.put(lineContents[2], lineContents[3]);

		}

		in.close();
		for (String c:categories.keySet()){
			pcategory = new ProductCategory(c, "PODdb", categories.get(c));
			categoryList.add(pcategory);
		}
		return categoryList;

	}

	static ArrayList<Product> getProducts() throws IOException{

		BufferedReader in = new BufferedReader(new FileReader(new File(DB_INITIAL_DATA_LOCATION+"productsCleanedWithCategoriesAndImageSelectedFieldsPlusBrand.csv")));
		String line;
		ArrayList<Product> products = new ArrayList<Product>();

		String[] lineContents;

		while ((line = in.readLine()) != null) {

			lineContents = line.split(";");
			Product p = new Product(lineContents[1], lineContents[0],lineContents[4],lineContents[5],lineContents[6],lineContents[7],lineContents[8],lineContents[9], lineContents[10],lineContents[11],lineContents[3],lineContents[12],lineContents[2]);
			products.add(p);
		}

		in.close();
		return products;

	}


}
