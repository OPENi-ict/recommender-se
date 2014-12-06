package models;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import tempservices.FSQ_Authorization;

public class Person {
	
	private String gender;
	private String age;
	private String education;
	private String[] interests;
	private String married;
	private String children_num;
	
	public Person(String filePath){
		
		Properties props = new Properties();
		InputStream in = FSQ_Authorization.class
				.getResourceAsStream(filePath);

		try {
			props.load(in);
			gender = props.getProperty("gender");
			age = props.getProperty("age");
			education = props.getProperty("education");
			String interestsAll = props.getProperty("interests");
			interests = interestsAll.split(",");
			married = props.getProperty("married");
			children_num = props.getProperty("number");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getGender() {
		return gender;
	}

	public String getAge() {
		return age;
	}

	public String getEducation() {
		return education;
	}

	public String[] getInterests() {
		return interests;
	}
	
	public String getMarried() {
		return married;
	}
	public String getChildrenNum() {
		return children_num;
	}
	

}
