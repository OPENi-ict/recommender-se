package models;

public class ContextPermissions {
	
	private boolean useInterests;
	private boolean useAge;
	private boolean useEducation;
	private boolean useGender;
	private boolean useCountry;
	private boolean useEthnicity;
	private boolean useIncome;
	private boolean useChildrenNumber;
	private boolean useMarriageState;
	private boolean useTime;
	
	public ContextPermissions(boolean useInterests, boolean useAge,
			boolean useEducation, boolean useGender, boolean useCountry,
			boolean useEthnicity, boolean useIncome, boolean useChildrenNumber,
			boolean useMarriageState, boolean useTime) {
		super();
		this.useInterests = useInterests;
		this.useAge = useAge;
		this.useEducation = useEducation;
		this.useGender = useGender;
		this.useCountry = useCountry;
		this.useEthnicity = useEthnicity;
		this.useIncome = useIncome;
		this.useChildrenNumber = useChildrenNumber;
		this.useMarriageState = useMarriageState;
		this.useTime = useTime;
	}
	
	
	public boolean isUseInterests() {
		return useInterests;
	}

	public boolean isUseAge() {
		return useAge;
	}

	public boolean isUseEducation() {
		return useEducation;
	}

	public boolean isUseGender() {
		return useGender;
	}

	public boolean isUseCountry() {
		return useCountry;
	}

	public boolean isUseEthnicity() {
		return useEthnicity;
	}

	public boolean isUseIncome() {
		return useIncome;
	}

	public boolean isUseChildrenNumber() {
		return useChildrenNumber;
	}

	public boolean isUseMarriageState() {
		return useMarriageState;
	}

	public boolean isUseTime() {
		return useTime;
	}
	
	
	

}
