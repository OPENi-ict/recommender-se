package control.products;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


public class ProductFileParser {

	//hashmap: [category name]-->[category code]
	static ArrayList<ProductCategory> getProductCategories() throws IOException, URISyntaxException{

		BufferedReader in = new BufferedReader(new FileReader(new File("C:\\Users\\Evmorfia\\Downloads\\productsCleanedWithCategoriesAndImageSelectedFieldsPlusBrand.csv")));

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

	static ArrayList<Product> getProducts() throws IOException, URISyntaxException{

		//		URI file_path = ProductFileParser.class.getResource("/productsCleanedWithCategoriesAndImageSelectedFieldsPlusBrand.csv").toURI();
		BufferedReader in = new BufferedReader(new FileReader(new File("C:\\Users\\Evmorfia\\Downloads\\productsCleanedWithCategoriesAndImageSelectedFieldsPlusBrand.csv")));
		String line;
		ArrayList<Product> products = new ArrayList<Product>();

		String[] lineContents;

		while ((line = in.readLine()) != null) {

			lineContents = line.split(";");
			System.out.println(lineContents.length);
			System.out.println(line);
			Product p = new Product(lineContents[1], lineContents[0],lineContents[4],lineContents[5],lineContents[6],lineContents[7],lineContents[8],lineContents[9], lineContents[10],lineContents[11],lineContents[3],lineContents[12],lineContents[2]);
			products.add(p);
		}

		in.close();
		return products;

	}


}
