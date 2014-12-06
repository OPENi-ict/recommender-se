package control.products;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import control.InitDB;
import dbtransactions.DatabaseUtils;

public class ProductBatchInitializer {

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

	public static void initialize() throws IOException, URISyntaxException{

		System.out.println("*** Product categories creation... ***");
		createProductCategoriesFromFile();
		System.out.println("*** Product categories created. ***");
		System.out.println("*** Products creation... ***");
		createProductsFromFile();
		System.out.println("*** Products created. ***");

	}

	public static void createProductsFromFile() throws IOException, URISyntaxException{
		ArrayList<Product> products = ProductFileParser.getProducts();
		importProducts(products);
	}

	public static void createProductCategoriesFromFile() throws IOException, URISyntaxException{
		ArrayList<ProductCategory> categories = ProductFileParser.getProductCategories();
		importProductCategories(categories);

	}


	private static HashMap<String,Long> getCategoryMap(){

		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		ExecutionEngine engine = new ExecutionEngine( graphDb );

		ExecutionResult result;
		String code;
		Long id;
		HashMap<String,Long> categories = new HashMap<String, Long>();
		Transaction tx = graphDb.beginTx();
		try {
			result = engine.execute( "MATCH (n:ProductCategory) RETURN n.code,id(n)" );

			ResourceIterator<Map<String,Object>> ri = result.iterator();
			while (ri.hasNext()){
				Map<String,Object> map = ri.next();
				code = (String)map.get("n.code");
				id = (Long)map.get("id(n)");
				categories.put(code, id);

			}

			tx.success();
			graphDb.shutdown();

			return categories;

		}catch (Exception E) {
			tx.failure();
			E.printStackTrace();
			return null;
		} finally {
			tx.close();
			graphDb.shutdown();
		}

	} 

	private static void importProducts(ArrayList<Product> plist){
		HashMap<String,Long> categories = getCategoryMap();

		BatchInserter inserter = BatchInserters.inserter( DB_PATH);
		Label productLabel = DatabaseUtils.LabelTypes.Product;
		RelationshipType belongs = DatabaseUtils.RelTypes.BELONGS;


		inserter.createDeferredSchemaIndex( productLabel ).on( "product_code" ).create();
		for (Product product:plist){
			HashMap<String, Object> properties = new HashMap<String,Object>();
			properties.put("product_code", product.getProduct_code());
			properties.put("product_name", product.getProduct_name());
			properties.put("image", product.getImage());
			properties.put("brand_code", product.getBrand_code());
			properties.put("brand", product.getBrand());
			properties.put("brand_owner", product.getBrand_owner());
			properties.put("gln", product.getGln());
			properties.put("gln_country_code", product.getGln_country_code());
			properties.put("registration", product.getRegistration());
			properties.put("registration_iso_code", product.getRegistration_iso_code());
			properties.put("brand_image", product.getBrand_image());
			properties.put("category_name", product.getCategory_name());
			properties.put("category_code", product.getCategory_code());
			Long productNodeId = inserter.createNode( properties, productLabel );

			Long categoryNodeId = categories.get(product.getCategory_code());
			inserter.createRelationship( productNodeId, categoryNodeId, belongs, null );
		}
		inserter.shutdown();

	}

	private static void importProductCategories(ArrayList<ProductCategory> pclist){

		BatchInserter inserter = BatchInserters.inserter( DB_PATH);
		Label productCategoryLabel = DatabaseUtils.LabelTypes.ProductCategory;
		Label CategoryLabel = DatabaseUtils.LabelTypes.Category;        

		inserter.createDeferredSchemaIndex( productCategoryLabel ).on( "code" ).create();
		for (ProductCategory pcategory:pclist){

			HashMap<String, Object> properties = new HashMap<String,Object>();
			properties.put("code", pcategory.getCode());
			properties.put("name", pcategory.getName());
			properties.put("provenance", pcategory.getProvenance());

			inserter.createNode( properties, productCategoryLabel, CategoryLabel );
		}
		inserter.shutdown();
	}



}
