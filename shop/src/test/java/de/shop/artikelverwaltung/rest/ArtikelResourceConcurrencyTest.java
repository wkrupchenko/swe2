package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestKonstanten.ACCEPT;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_ID_PATH;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_PATH;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_PATH;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKEL_PATH;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentDelete;
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceConcurrencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(500);
	private static final Long ARTIKEL_ID_DELETE_1 = Long.valueOf(505);
	private static final Long ARTIKEL_ID_DELETE_2 = Long.valueOf(506);
	private static final Long ARTIKELGRUPPE_ID_UPDATE = Long.valueOf(400);
	private static final Long ARTIKELGRUPPE_ID_DELETE_1 = Long.valueOf(405);
	private static final Long ARTIKELGRUPPE_ID_DELETE_2 = Long.valueOf(403);
	private static final String NEUE_BEZEICHNUNG = "Update1Artikel";
	private static final String NEUE_BEZEICHNUNG_2 = "Update2Artikel";
	private static final String NEUE_BEZEICHNUNG_ARTIKELGRUPPE = "Update1Artikelgruppe";
	private static final String NEUE_BEZEICHNUNG_2_ARTIKELGRUPPE = "Update2Artikelgruppe";
	
	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN Test updateUpdate");
		
		// Given
		final Long artikelId = ARTIKEL_ID_UPDATE;
    	final String neueBezeichnung = NEUE_BEZEICHNUNG;
    	final String neueBezeichnung2 = NEUE_BEZEICHNUNG_2;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

    	// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuer Bezeichnung bauen
    	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung2);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, ARTIKEL_PATH,
    			                                                       username, password);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	job = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
		                  .auth()
		                  .basic(username, password)
		                  .put(ARTIKEL_PATH);
    	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.debugf("ENDE Test updateUpdate");
	}
	
	@Test
	public void updateUpdateArtikelgruppe() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN Test updateUpdateArtikelgruppe");
		
		// Given
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_UPDATE;
    	final String neueBezeichnung = NEUE_BEZEICHNUNG_ARTIKELGRUPPE;
    	final String neueBezeichnung2 = NEUE_BEZEICHNUNG_2_ARTIKELGRUPPE;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
                                   .get(ARTIKELGRUPPE_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

    	// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuer Bezeichnung bauen
    	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung2);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, ARTIKELGRUPPE_PATH,
    			                                                       username, password);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuer Bezeichnung bauen
    	job = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
		                  .auth()
		                  .basic(username, password)
		                  .put(ARTIKELGRUPPE_PATH);
    	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.debugf("ENDE Test updateUpdateArtikelgruppe");
	}
	
	@Test
	public void updateDelete() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN Test updateDelete");
		
		// Given
		final Long artikelId = ARTIKEL_ID_DELETE_1;
    	final String neueBezeichnung = NEUE_BEZEICHNUNG;
		final String username = USERNAME;
		final String password = PASSWORD;
		final String username2 = USERNAME_ADMIN;
		final String password2 = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Delete
    	final ConcurrentDelete concurrentDelete = new ConcurrentDelete(ARTIKEL_PATH + '/' + artikelId,
    			                                                       username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentDelete);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	response = given().contentType(APPLICATION_JSON)
    			          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(ARTIKEL_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		
		LOGGER.debugf("ENDE Test updateDelete");
	}
	
	@Test
	public void updateDeleteArtikelgruppe() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN Test updateDeleteArtikelgruppe");
		
		// Given
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_DELETE_1;
    	final String neueBezeichnung = NEUE_BEZEICHNUNG_ARTIKELGRUPPE;
		final String username = USERNAME;
		final String password = PASSWORD;
		final String username2 = USERNAME_ADMIN;
		final String password2 = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
                                   .get(ARTIKELGRUPPE_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Delete
    	final ConcurrentDelete concurrentDelete = new ConcurrentDelete(ARTIKELGRUPPE_PATH + '/' + artikelgruppeId,
    			                                                       username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentDelete);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	response = given().contentType(APPLICATION_JSON)
    			          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(ARTIKELGRUPPE_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		
		LOGGER.debugf("ENDE Test updateDeleteArtikelgruppe");
	}
	
	@Test
	public void deleteUpdate() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN Test deleteUpdate");
		
		// Given
		final Long artikelId = ARTIKEL_ID_DELETE_2;
    	final String neueBezeichnung = NEUE_BEZEICHNUNG;
    	final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		final String username2 = USERNAME;
		final String password2 = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Update
		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final ConcurrentUpdate concurrenUpdate = new ConcurrentUpdate(jsonObject, ARTIKEL_PATH,
    			                                                      username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrenUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Erfolgreiches Delete trotz konkurrierendem Update
		response = given().auth()
                          .basic(username, password)
                          .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                          .delete(ARTIKEL_ID_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		LOGGER.debugf("ENDE Test deleteUpdate");
	}
	
	@Test
	public void deleteUpdateArtikelgruppe() throws InterruptedException, ExecutionException {
		LOGGER.debugf("BEGINN Test deleteUpdateArtikelgruppe");
		
		// Given
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_DELETE_2;
    	final String neueBezeichnung = NEUE_BEZEICHNUNG_ARTIKELGRUPPE;
    	final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		final String username2 = USERNAME;
		final String password2 = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
                                   .get(ARTIKELGRUPPE_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Update
		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final ConcurrentUpdate concurrenUpdate = new ConcurrentUpdate(jsonObject, ARTIKELGRUPPE_PATH,
    			                                                      username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrenUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Erfolgreiches Delete trotz konkurrierendem Update
		response = given().auth()
                          .basic(username, password)
                          .pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
                          .delete(ARTIKELGRUPPE_ID_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		LOGGER.debugf("ENDE Test deleteUpdateArtikelgruppe");
	}
	
}
