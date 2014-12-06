package dbtransactions;


import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import control.places.PlaceCategory;
import control.products.Product;


public class DatabaseUtils {

	private String db_path;
	private GraphDatabaseService graphDb;
	private Index<Node> placesIndex;

	public static enum RelTypes implements RelationshipType {

		SUBCATEGORY, BELONGS
	}
	
	public static enum LabelTypes implements Label {

		TopPlaceType, PlaceType, PlaceCategory, Category, ProductCategory, Product
	}

	
	
	public DatabaseUtils(String db_path) {
		this.db_path = db_path;
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(db_path);
		Transaction tx = graphDb.beginTx();
		try {
			IndexManager index = graphDb.index();
			this.placesIndex = index.forNodes( "placesFOURSQUARE" );
			tx.success();
		} catch (Exception E) {
			tx.failure();
			E.printStackTrace();
		} finally {
		tx.close();
	}
		

	}
	
	public void shutDownDB(){
		this.db_path = null;
		graphDb.shutdown();
	}

	public String getDB_path() {
		return this.db_path;
	}
	
    public void createPlaceType(PlaceCategory pcat){
		
		Node n;
		Transaction tx = graphDb.beginTx();
		try {
			n = graphDb.createNode();
			n.addLabel(pcat.getLevelLabel());
			n.addLabel(LabelTypes.Category);
			n.addLabel(LabelTypes.PlaceCategory);
			n.setProperty("provenance",pcat.getProvenance());
			n.setProperty("name", pcat.getName());
			n.setProperty("code", pcat.getCode());
			placesIndex.add( n, "name", n.getProperty( "name" ) );
			tx.success();
//			System.out.println("Node successfully created");
		} catch (Exception E) {
			tx.failure();
			System.out.println("oups...node creation not performed");
		} finally {
			tx.close();
		}
		
	}
	
    public void createProductCategory(String name, String code, String provenance){
		
		Node n;
		Transaction tx = graphDb.beginTx();
		try {
			IndexManager index = graphDb.index();
			Index<Node> categories = index.forNodes( "productCategories" );
			n = graphDb.createNode();
			n.addLabel(LabelTypes.Category);
			n.addLabel(LabelTypes.ProductCategory);
			n.setProperty("name", name);
			n.setProperty("code", code);
			n.setProperty("provenance", provenance);
			categories.add( n, "code", code );
			tx.success();
			System.out.println("Node successfully created");
		} catch (Exception E) {
			tx.failure();
			System.out.println("oups...node creation not performed");
		} finally {
			tx.close();
		}
		
	}
    
    public void createProduct(Product product){
		
    	Node n;
		Transaction tx = graphDb.beginTx();
		try {
			IndexManager index = graphDb.index();
			Index<Node> categories = index.forNodes( "productCategories" );
			n = graphDb.createNode();
			n.addLabel(LabelTypes.Product);
			n.setProperty("product_code", product.getProduct_code());
			n.setProperty("product_name", product.getProduct_name());
			n.setProperty("image", product.getImage());
			n.setProperty("brand_code", product.getBrand_code());
			n.setProperty("brand", product.getBrand());
			n.setProperty("brand_owner", product.getBrand_owner());
			n.setProperty("gln", product.getGln());
			n.setProperty("gln_country_code", product.getGln_country_code());
			n.setProperty("registration", product.getRegistration());
			n.setProperty("registration_iso_code", product.getRegistration_iso_code());
			
			IndexHits<Node> hits = categories.get( "code", product.getCategory_code() );
			Node category = hits.getSingle();
			
			n.createRelationshipTo(category,RelTypes.BELONGS);
			
			tx.success();
			System.out.println("Node successfully created");
		} catch (Exception E) {
			tx.failure();
			System.out.println("oups...node creation not performed");
			E.printStackTrace();
			System.exit(1);
		} finally {
			tx.close();
		}
		
	}
	
	public void createPlaceSubcategoryRelationship(String nodeTo, String nodeFrom, RelationshipType reltype){
		
		Transaction tx = graphDb.beginTx();
		try {
			IndexHits<Node> hits  = placesIndex.get("name", nodeFrom);
			Node firstNode =  hits.getSingle();
			hits  = placesIndex.get("name", nodeTo);
			Node secondNode =  hits.getSingle();
			Relationship myRelationship = firstNode.createRelationshipTo(secondNode,reltype);
			myRelationship.setProperty("importance", 1 );
			tx.success();
//			System.out.println("relationship created successfully");
		} catch (Exception E) {
			tx.failure();
			E.printStackTrace();
		} finally {
			tx.close();
		}
		
	}

	
	public ExecutionResult executeQuery(String query){
	
	ExecutionEngine engine = new ExecutionEngine( graphDb );

	ExecutionResult result;
	Transaction tx = graphDb.beginTx();
	try {
	    result = engine.execute( query );
	    tx.success();
	    return result;
	
	   
	}catch (Exception E) {
		tx.failure();
		E.printStackTrace();
		System.out.println("oupsss...executeQuery problem");
	} finally {
		tx.close();
	}
	return null;
	
	}
	
	public static ExecutionResult executeQuery(String query,GraphDatabaseService graphDb){
		
		ExecutionEngine engine = new ExecutionEngine( graphDb );

		ExecutionResult result;
		Transaction tx = graphDb.beginTx();
		try {
		    result = engine.execute( query );
		    tx.success();
		    return result;
		
		   
		}catch (Exception E) {
			tx.failure();
			E.printStackTrace();
			System.out.println("oupsss...executeQuery problem");
		} finally {
			tx.close();
		}
		return null;
		
		}

}