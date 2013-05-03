package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_URI;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_PATH;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_ID_PATH;
import static de.shop.util.TestKonstanten.ACCEPT;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_PATH;
import static de.shop.util.TestKonstanten.ARTIKEL_PATH;
import static de.shop.util.TestKonstanten.ARTIKEL_BEZEICHNUNG_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKEL_ERHAELTLICH_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_BEZEICHNUNG_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_FILE_PATH;
import static de.shop.util.TestKonstanten.LOCATION;
import static de.shop.util.TestKonstanten.BASEPATH;
import static de.shop.util.TestKonstanten.BASEURI;
import static de.shop.util.TestKonstanten.PORT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.xml.bind.DatatypeConverter;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;

import com.jayway.restassured.response.Response;

import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.AbstractResourceTest;
import de.shop.util.NoMimeTypeException;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String ARTIKEL_BEZEICHNUNG_VORHANDEN = "Schlauchschal";
	private static final String ARTIKEL_BEZEICHNUNG_NICHT_VORHANDEN = "Rosa Shirt";
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(500);
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long ARTIKELGRUPPE_ID_ZU_ARTIKEL_500 = Long.valueOf(400);
	private static final String ARTIKEL_BEZEICHNUNG_NEU = "Gelbes Shirt";
	private static final Boolean ARTIKEL_ERHAELTLICH_NEU = true;
	private static final double ARTIKEL_PREIS_NEU = 15.99;
	private static final Long ARTIKELGRUPPE_ID_VORHANDEN = Long.valueOf(404);
	private static final String ARTIKELGRUPPE_BEZEICHNUNG_NEU = "Unterwäsche";
	private static final String ARTIKELGRUPPE_NAME_VORHANDEN = "Sommermode";	 
	private static final String ARTIKELGRUPPE_NAME_NICHT_VORHANDEN = "XXX";
	private static final Long ARTIKELGRUPPE_ID_NICHT_VORHANDEN = Long.valueOf(19392);
	private static final Long ARTIKEL1_ZU_ARTIKELGRUPPE_406 = Long.valueOf(507);
	private static final Long ARTIKELGRUPPE_ID_VORHANDEN2 = Long.valueOf(406);
	private static final Long ARTIKEL_ID_LÖSCHEN = Long.valueOf(504);
	private static final Long ARTIKEL_ID_MIT_BESTELLUNGEN = Long.valueOf(501);
	private static final Long ARTIKELGRUPPE_ID_MIT_ARTIKEL = Long.valueOf(404);
	private static final Long ARTIKELGRUPPE_ID_LÖSCHEN = Long.valueOf(403);
	private static final Long ARTIKELGRUPPE_ID_UPDATE = Long.valueOf(400);
	private static final String ARTIKELGRUPPE_NEUE_BEZEICHNUNG = "Bezeichnungsänderung";
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(503);
	private static final String ARTIKEL_NEUE_BEZEICHNUNG = "Neuer Name für Artikel";
	private static final String VERFUEGBARKEIT = "true";
	private static final Long ARTIKEL_ID_UPLOAD = Long.valueOf(500);
	private static final String FILENAME = "image.gif";
	private static final String FILENAME_UPLOAD = "src/test/resources/rest/" + FILENAME;
	private static final CopyOption[] COPY_OPTIONS = { REPLACE_EXISTING };
	private static final String FILENAME_DOWNLOAD = "target/" + FILENAME;
	private static final String FILENAME_INVALID_MIMETYPE = "image.bmp";
	private static final String FILENAME_UPLOAD_INVALID_MIMETYPE = "src/test/resources/rest/" + FILENAME_INVALID_MIMETYPE;
	
	@Inject
	private ArtikelService as;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findeArtikelNachBezeichnungVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachBezeichnungVorhanden");
		
		// GIVEN
		final String bezeichnung = ARTIKEL_BEZEICHNUNG_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
							.queryParam(ARTIKEL_BEZEICHNUNG_PATH_PARAM, bezeichnung)
							.get(ARTIKEL_PATH);
		
		// THEN
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for(JsonObject jsonObject : jsonObjectList)
				assertThat(jsonObject.getString("bezeichnung"), is(bezeichnung));
		}
		LOGGER.debugf("ENDE Test findeArtikelNachBezeichnungVorhanden");
	}
	
	@Test
	public void findeArtikelNachBezeichnungNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachBezeichnungNichtVorhanden");
		
		// GIVEN
		final String artikelBezeichnung = ARTIKEL_BEZEICHNUNG_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.queryParam(ARTIKEL_BEZEICHNUNG_PATH_PARAM, artikelBezeichnung)
									.get(ARTIKEL_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelNachBezeichnungNichtVorhanden");
	}
	
	@Test
	public void findeArtikelNachIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachIdVorhanden");
		
		// GIVEN
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
									.get(ARTIKEL_ID_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
		}
		LOGGER.debugf("ENDE Test findeArtikelNachIdVorhanden");
	}
	
	@Test
	public void findeArtikelNachIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachIdNichtVorhanden");
		
		// GIVEN
		final Long artikelId = ARTIKEL_ID_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
									.get(ARTIKEL_ID_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelNachIdNichtVorhanden");
	}
	
	@Test
	public void findeArtikelNachArtikelgruppeVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachArtikelgruppeVorhanden");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_VORHANDEN2;
		final List<Long> artikelId = new ArrayList<Long>();
		artikelId.add(ARTIKEL1_ZU_ARTIKELGRUPPE_406);
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
									.get(ARTIKELGRUPPE_ID_PATH + "/artikel");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			for(int i = 0; i < jsonArray.size(); ++i) {
				assertThat(artikelId.contains(jsonArray.getJsonObject(i).getJsonNumber("id").longValue()), is(true));
			}
		}
		LOGGER.debugf("ENDE Test findeArtikelNachArtikelgruppeVorhanden");
	}
	
	@Test
	public void findeArtikelNachArtikelgruppeNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachArtikelgruppeNichtVorhanden");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
									.get(ARTIKELGRUPPE_ID_PATH + "/artikel");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelNachArtikelgruppeVorhanden");
	}
	
	@Test
	public void findeArtikelNachVerfuegbarkeit() {
		LOGGER.debugf("BEGINN Test findeArtikelNachVerfuegbarkeit");
		
		// GIVEN
		final String verfuegbarkeit = VERFUEGBARKEIT;
		final boolean erhaeltlich;
		if("true".equals(verfuegbarkeit))
			erhaeltlich = true;
		else
			erhaeltlich = false;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
							.pathParam(ARTIKEL_ERHAELTLICH_PATH_PARAM, verfuegbarkeit)
							.get(ARTIKEL_PATH + "/{erhaeltlich}");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			for(int i = 0; i < jsonArray.size(); ++i) {
				assertThat(jsonArray.getJsonObject(i).getBoolean("erhaeltlich"), is(erhaeltlich));
			}
		}
		LOGGER.debugf("ENDE Test findeArtikelNachVerfuegbarkeit");
	}
	
	@Test
	public void createArtikel() {
		
		LOGGER.debugf("BEGINN Test createArtikel");
		// GIVEN
		final String bezeichnung = ARTIKEL_BEZEICHNUNG_NEU;  
		final Boolean erhaeltlich = ARTIKEL_ERHAELTLICH_NEU;  
		final double preis = ARTIKEL_PREIS_NEU;  
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_VORHANDEN;
		final String username = USERNAME;
		final String password = PASSWORD;
			
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
								.add("bezeichnung", bezeichnung)
								.add("erhaeltlich", erhaeltlich)
								.add("preis", preis)
								.add("artikelgruppe", ARTIKELGRUPPE_URI + "/" + artikelgruppeId ) 
								.build();
		
		// WHEN
		Response response = given()
							.contentType(APPLICATION_JSON).body(jsonObject.toString())
							.auth()
							.basic(username, password)
							.post(ARTIKEL_PATH);

		// THEN
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader("Location");
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		LOGGER.debugf("ENDE Test createArtikel");
	}
	
	
	@Test
	public void updateArtikel() {
		LOGGER.debugf("BEGINN Test updateArtikel");
		
		// Given
		final Long artikelId = ARTIKEL_ID_UPDATE;
		final String neueBezeichnung = ARTIKEL_NEUE_BEZEICHNUNG;
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
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
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
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
				          .auth()
				          .basic(username, password)
                          .put(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.debugf("ENDE Test updateArtikel");
   	}
	
	@Test
	public void deleteArtikelMitBestellungen() {
		LOGGER.debugf("BEGINN Test deleteArtikelMitBestellungen");
		
		// GIVEN
		final Long artikelId = ARTIKEL_ID_MIT_BESTELLUNGEN;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// WHEN
		final Response response = given().pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
										 .auth()
										 .basic(username, password).delete(ARTIKEL_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		final String errorMsg = response.asString();
		assertThat(errorMsg, startsWith("Artikel mit ID=" + artikelId + " kann nicht geloescht werden, es sind Bestellungen vorhanden!"));
		LOGGER.debugf("ENDE Test deleteArtikelMitBestellungen");
	}
	
	@Test
	public void deleteArtikel() {
		LOGGER.debugf("BEGINN Test deleteArtikel");
		
		// GIVEN
		final Long artikelId = ARTIKEL_ID_LÖSCHEN;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// WHEN
		final Response response = given().pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
										 .auth()
										 .basic(username, password).delete(ARTIKEL_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.debugf("ENDE Test deleteArtikel");
	}
	
	@Test
	public void findeArtikelgruppeNachBezeichnungVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachBezeichnungVorhanden");
		
		// GIVEN
		final String bezeichnung = ARTIKELGRUPPE_NAME_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.queryParam(ARTIKELGRUPPE_BEZEICHNUNG_PATH_PARAM, bezeichnung)
									.get(ARTIKELGRUPPE_PATH);
		
		// THEN
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for(JsonObject jsonObject : jsonObjectList) {
				assertThat(jsonObject.getString("bezeichnung"), is(bezeichnung));
			}
		}
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachBezeichnungVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachBezeichnungNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachBezeichnungNichtVorhanden");
		
		// GIVEN
		final String artikelgruppeName = ARTIKELGRUPPE_NAME_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.queryParam(ARTIKELGRUPPE_BEZEICHNUNG_PATH_PARAM, artikelgruppeName)
									.get(ARTIKELGRUPPE_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachBezeichnungNichtVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachIdVorhanden");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
									.get(ARTIKELGRUPPE_ID_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelgruppeId.longValue()));
		}
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachIdVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachIdNichtVorhanden");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
									.get(ARTIKELGRUPPE_ID_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachIdNichtVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachArtikelVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachArtikelVorhanden");
		
		// GIVEN
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_ZU_ARTIKEL_500;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
									.get(ARTIKEL_ID_PATH + "/artikelgruppe");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelgruppeId.longValue()));
		}
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachArtikelVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachArtikelNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachArtikelNichtVorhanden");
		
		// GIVEN
		final Long artikelId = ARTIKEL_ID_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON)
									.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
									.get(ARTIKEL_ID_PATH + "/artikelgruppe");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachArtikelVorhanden");
	}
	
	@Test
	public void createArtikelgruppe() {
		LOGGER.debugf("BEGINN Test createArtikelgruppe");
		// GIVEN
		final String bezeichnung = ARTIKELGRUPPE_BEZEICHNUNG_NEU;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				.add("bezeichnung", bezeichnung)
				.build();
		
		// WHEN
		Response response = given().contentType(APPLICATION_JSON).body(jsonObject.toString())
								   .auth()
								   .basic(username, password).post(ARTIKELGRUPPE_PATH);
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader("Location");
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		LOGGER.debugf("ENDE Test createArtikelgruppe");
	}
	
	@Test
	public void updateArtikelgruppe() {
		LOGGER.debugf("BEGINN Test updateArtikelgruppe");
		
		// Given
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_UPDATE;
		final String neueBezeichnung = ARTIKELGRUPPE_NEUE_BEZEICHNUNG;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
				                   .auth()
				                   .basic(username, password)
                                   .get(ARTIKELGRUPPE_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelgruppeId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
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
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .put(ARTIKELGRUPPE_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.debugf("ENDE Test updateArtikelgruppe");
   	}
	
	@Test
	public void deleteArtikelgruppeMitArtikel() {
		LOGGER.debugf("BEGINN Test deleteArtikelgruppeMitArtikel");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_MIT_ARTIKEL;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// WHEN
		final Response response = given().pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
										 .auth()
										 .basic(username, password).delete(ARTIKELGRUPPE_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		final String errorMsg = response.asString();
		assertThat(errorMsg, startsWith("Artikelgruppe mit ID=" + artikelgruppeId + " kann nicht geloescht werden, es sind Artikel vorhanden!"));
		LOGGER.debugf("ENDE Test deleteArtikelgruppeMitArtikel");
	}
	
	@Test
	public void deleteArtikelgruppe() {
		LOGGER.debugf("BEGINN Test deleteArtikelgruppe");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_LÖSCHEN;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// WHEN
		final Response response = given().pathParameter(ARTIKELGRUPPE_ID_PATH_PARAM, artikelgruppeId)
										 .auth()
										 .basic(username, password).delete(ARTIKELGRUPPE_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.debugf("ENDE Test deleteArtikelgruppe");
	}
	
	@Test
	public void uploadDownload() throws IOException {
		LOGGER.debugf("BEGINN Test uploadDownload");
		
		// Given
		final Long artikelId = ARTIKEL_ID_UPLOAD;
		final String fileName = FILENAME_UPLOAD;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// Datei als byte[] einlesen
		byte[] bytes;
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			Files.copy(Paths.get(fileName), stream);
			bytes = stream.toByteArray();
		}
		
		// byte[] als Inhalt eines JSON-Datensatzes mit Base64-Codierung
		JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
	                            .add("bytes", DatatypeConverter.printBase64Binary(bytes))
	                            .build();
		
		// When
		Response response = given().contentType(APPLICATION_JSON)
				                   .body(jsonObject.toString())
                                   .auth()
                                   .basic(username, password)
                                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .post(ARTIKEL_ID_FILE_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		// id extrahieren aus http://localhost:8080/shop2/rest/artikel/<id>/file
		final String idStr = response.getHeader(LOCATION)
				                     .replace(BASEURI + ":" + PORT + BASEPATH + ARTIKEL_PATH + '/', "")
				                     .replace("/file", "");
		assertThat(idStr, is(artikelId.toString()));
		
		// When (2)
		// Download der zuvor hochgeladenen Datei
		response = given().header(ACCEPT, APPLICATION_JSON)
				          .auth()
                          .basic(username, password)
                          .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                          .get(ARTIKEL_ID_FILE_PATH);
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
		final String base64String = jsonObject.getString("bytes");
		final byte[] downloaded = DatatypeConverter.parseBase64Binary(base64String);
		
		// Then (2)
		// Dateigroesse vergleichen: hochgeladene Datei als byte[] einlesen
		byte[] uploaded;
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			Files.copy(Paths.get(fileName), stream);
			uploaded = stream.toByteArray();
		}
		assertThat(uploaded.length, is(downloaded.length));
		
		// Abspeichern der heruntergeladenen Datei im Unterverzeichnis target zur manuellen Inspektion
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(downloaded)) {
			Files.copy(inputStream, Paths.get(FILENAME_DOWNLOAD), COPY_OPTIONS);
		}

		LOGGER.info("Heruntergeladene Datei abgespeichert: " + FILENAME_DOWNLOAD);
		LOGGER.debugf("ENDE Test uploadDownload");
	}
	
	@Test
	public void uploadInvalidMimeType() throws IOException {
		LOGGER.debugf("BEGINN Test uploadInvalidMimeType");
		
		// Given
		final Long artikelId = ARTIKEL_ID_UPLOAD;
		final String fileName = FILENAME_UPLOAD_INVALID_MIMETYPE;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// Datei als byte[] einlesen
		byte[] bytes;
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			Files.copy(Paths.get(fileName), stream);
			bytes = stream.toByteArray();
		}
		
		// byte[] als Inhalt eines JSON-Datensatzes mit Base64-Codierung
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
	                                  .add("bytes", DatatypeConverter.printBase64Binary(bytes))
	                                  .build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
				                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
				                         .post(ARTIKEL_ID_FILE_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		assertThat(response.asString(), is(NoMimeTypeException.MESSAGE));
		
		LOGGER.debugf("ENDE Test uploadInvalidMimeType");
	}
}

