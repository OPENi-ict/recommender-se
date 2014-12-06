package control.products;

public class ProductCategory {
	
	private String name;
	private String code;
	private String provenance;
	
	
	public ProductCategory(String name, String provenance, String code) {
		super();
		this.name = name;
		this.provenance = provenance;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	public String getCode() {
		return code;
	}
	
	public String getProvenance() {
		return provenance;
	}
	

}
