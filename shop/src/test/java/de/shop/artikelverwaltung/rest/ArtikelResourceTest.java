package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static de.shop.util.TestKonstanten.ARTIKELGRUPPE_URI;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;

import com.jayway.restassured.response.Response;

import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String ARTIKEL_BEZEICHNUNG_VORHANDEN = "Pollunder";
	private static final String ARTIKEL_BEZEICHNUNG_NICHT_VORHANDEN = "Rosa Shirt";
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(500);
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long ARTIKELGRUPPE_ID_ZU_ARTIKEL_500 = Long.valueOf(400);
	private static final String ARTIKEL_BEZEICHNUNG_NEU = "Gelbes Shirt";
	private static final Boolean ARTIKEL_ERHAELTLICH_NEU = true;
	private static final double ARTIKEL_PREIS_NEU = 15.99;
	private static final Long ARTIKELGRUPPE_ID_VORHANDEN = Long.valueOf(400);
	private static final String ARTIKELGRUPPE_BEZEICHNUNG_NEU = "Unterwäsche";
	private static final String ARTIKELGRUPPE_NAME_VORHANDEN = "Sommermode";
	private static final String ARTIKELGRUPPE_NAME_NICHT_VORHANDEN = "XXX";
	private static final Long ARTIKELGRUPPE_ID_NICHT_VORHANDEN = Long.valueOf(19392);
	private static final Long ARTIKEL1_ZU_ARTIKELGRUPPE_400 = Long.valueOf(500);
	private static final Long ARTIKEL2_ZU_ARTIKELGRUPPE_400 = Long.valueOf(504);
	private static final Long ARTIKEL3_ZU_ARTIKELGRUPPE_400 = Long.valueOf(505);
	
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
		final String artikelBezeichnung = ARTIKEL_BEZEICHNUNG_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).queryParam("bezeichnung", artikelBezeichnung)
									.get("/artikel");
		
		// THEN
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for(JsonObject jsonObject : jsonObjectList)
				assertThat(jsonObject.getString("bezeichnung"), is(artikelBezeichnung));
		}
		LOGGER.debugf("ENDE Test findeArtikelNachBezeichnungVorhanden");
	}
	
	@Test
	public void findeArtikelNachBezeichnungNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachBezeichnungNichtVorhanden");
		
		// GIVEN
		final String artikelBezeichnung = ARTIKEL_BEZEICHNUNG_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).queryParam("bezeichnung", artikelBezeichnung)
									.get("/artikel");
		
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
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("artikelId", artikelId)
									.get("/artikel/{artikelId}");
		
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
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("artikelId", artikelId)
									.get("/artikel/{artikelId}");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeArtikelNachIdNichtVorhanden");
	}
	
	@Test
	public void findeArtikelNachArtikelgruppeVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachArtikelgruppeVorhanden");
		
		// GIVEN
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_VORHANDEN;
		final List<Long> artikelId = new ArrayList<Long>();
		artikelId.add(ARTIKEL1_ZU_ARTIKELGRUPPE_400);
		artikelId.add(ARTIKEL2_ZU_ARTIKELGRUPPE_400);
		artikelId.add(ARTIKEL3_ZU_ARTIKELGRUPPE_400);
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("artikelgruppeId", artikelgruppeId)
									.get("/artikel/artikelgruppe/{artikelgruppeId}/artikel");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			for(int i = 0; i < jsonArray.size(); ++i)
				assertThat(artikelId.contains(jsonArray.getJsonObject(i).getJsonNumber("id").longValue()), is(true));
		}
		LOGGER.debugf("ENDE Test findeArtikelNachArtikelgruppeVorhanden");
	}
	
	@Ignore
	@Test
	public void createArtikel() {
		// TODO
		
		LOGGER.debugf("BEGINN Test createArtikel");
		// GIVEN
		final String bezeichnung = ARTIKEL_BEZEICHNUNG_NEU;  
		final Boolean erhaeltlich = ARTIKEL_ERHAELTLICH_NEU;  
		final double preis = ARTIKEL_PREIS_NEU;  
		final Long artikelgruppeId = ARTIKELGRUPPE_ID_VORHANDEN;  
			
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
								.add("bezeichnung", bezeichnung)
								.add("erhaeltlich", erhaeltlich)
								.add("preis", preis)
								.add("artikelgruppe", ARTIKELGRUPPE_URI + "/" + artikelgruppeId ) 
								.build();
		
		// WHEN
		Response response = given().contentType(APPLICATION_JSON).body(jsonObject.toString()).post("ARTIKEL_PATH");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader("Location");
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		LOGGER.debugf("ENDE Test createArtikel");
	}
	
	@Ignore
	@Test
	public void updateArtikel() {
		// TODO
	}
	
	@Ignore
	@Test
	public void deleteArtikel() {
		// TODO
	}
	
	@Test
	public void findeArtikelgruppeNachBezeichnungVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachBezeichnungVorhanden");
		
		// GIVEN
		final String bezeichnung = ARTIKELGRUPPE_NAME_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).queryParam("bezeichnung", bezeichnung)
									.get("/artikel/artikelgruppe");
		
		// THEN
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for(JsonObject jsonObject : jsonObjectList)
				assertThat(jsonObject.getString("bezeichnung"), is(bezeichnung));
		}
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachBezeichnungVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachBezeichnungNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachBezeichnungNichtVorhanden");
		
		// GIVEN
		final String artikelgruppeName = ARTIKELGRUPPE_NAME_NICHT_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).queryParam("name", artikelgruppeName)
									.get("/artikel/artikelgruppe");
		
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
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("artikelgruppeId", artikelgruppeId)
									.get("/artikel/artikelgruppe/{artikelgruppeId}");
		
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
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("artikelgruppeId", artikelgruppeId)
									.get("/artikel/artikelgruppe/{artikelgruppeId}");
		
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
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("artikelId", artikelId)
									.get("/artikel/{artikelId}/artikelgruppe");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader( new StringReader(response.asString()))) {
			JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelgruppeId.longValue()));
		}
		LOGGER.debugf("ENDE Test findeArtikelgruppeNachArtikelVorhanden");
	}
	
	@Ignore
	@Test
	public void createArtikelgruppe() {
		// TODO
		LOGGER.debugf("BEGINN Test createArtikelgruppe");
		// GIVEN
		final String bezeichnung = ARTIKELGRUPPE_BEZEICHNUNG_NEU;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				.add("bezeichnung", bezeichnung)
				.build();
		
		// WHEN
		Response response = given().contentType(APPLICATION_JSON).body(jsonObject.toString()).post("ARTIKELGRUPPE_PATH");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader("Location");
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		LOGGER.debugf("ENDE Test createArtikelgruppe");
	}
	
	@Ignore
	@Test
	public void updateArtikelgruppe() {
		// TODO
	}
	
	@Ignore
	@Test
	public void deleteArtikelgruppe() {
		// TODO
	}
}

