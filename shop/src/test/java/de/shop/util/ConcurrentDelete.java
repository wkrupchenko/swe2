package de.shop.util;

import static com.jayway.restassured.RestAssured.given;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;
import org.jboss.logging.Logger;

import com.jayway.restassured.response.Response;

public class ConcurrentDelete implements Callable<Response> {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private final String url;
	private final String username;
	private final String password;
	
	public ConcurrentDelete(String url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	public Response call() {
		LOGGER.debugf("BEGINN call");
		
		final Response response = given().auth()
                                         .basic(username, password)
                                         .delete(url);

		LOGGER.debugf("ENDE call");
		return response;
	}	
}
