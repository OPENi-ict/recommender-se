package control.places;

import dbtransactions.DatabaseUtils.LabelTypes;

public class PlaceCategory {
	
	private String name;
	private String provenance;
	private String code;
	private LabelTypes levelLabel;
	
	public PlaceCategory(String name, String provenance, String code, LabelTypes label) {
		super();
		this.name = name;
		this.provenance = provenance;
		this.code = code;
		this.levelLabel = label;
	}

	public String getName() {
		return name;
	}

	public String getProvenance() {
		return provenance;
	}

	public String getCode() {
		return code;
	}
	
	public LabelTypes getLevelLabel(){
		return levelLabel;
	}
	

}
