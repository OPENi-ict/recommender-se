package tempservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FSQ_Authorization {

	private static String auth;

	public FSQ_Authorization(){
		Properties props = new Properties();
		InputStream in = FSQ_Authorization.class
				.getResourceAsStream("/props.properties");

		try {
			props.load(in);
			auth = props.getProperty("authFSQ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	String getToken() {
		return auth;
	}

}
