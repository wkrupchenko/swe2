package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String ARTIKEL_BEZEICHNUNG_VORHANDEN = "Pollunder";
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(500);
	
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
		Response response = given().header("Accept", APPLICATION_JSON).queryParam("bezeichnung", bezeichnung)
									.get("/artikel");
		
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
	public void findeArtikelNachIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelNachIdVorhanden");
		
		// GIVEN
		final Long id = ARTIKEL_ID_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("id", id)
									.get("/artikel/{id}");
		
		// THEN
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(id.longValue()));
		}
		LOGGER.debugf("ENDE Test findeArtikelNachIdVorhanden");
	}
	
	@Test
	public void findeArtikelgruppeNachArtikelVorhanden() {
		LOGGER.debugf("BEGINN Test findeArtikelgruppeNachArtikelVorhanden");
		
		// GIVEN
		final Long id = ARTIKEL_ID_VORHANDEN;
		
		// WHEN
		Response response = given().header("Accept", APPLICATION_JSON).pathParameter("id", id)
									.get("/artikel/{id}/artikelgruppe");
	}
}
