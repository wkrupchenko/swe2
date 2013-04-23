package de.shop.bestellverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestKonstanten.ACCEPT;
import static de.shop.util.TestKonstanten.ARTIKEL_URI;
import static de.shop.util.TestKonstanten.BESTELLUNGEN_ID_PATH;
import static de.shop.util.TestKonstanten.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.BESTELLUNGEN_PATH;
import static de.shop.util.TestKonstanten.KUNDEN_URI;
import static de.shop.util.TestKonstanten.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.jboss.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class BestellungResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(300);
	private static final Long BESTELLUNG_ID_NICHT_VORHANDEN = Long.valueOf(700);
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);
	private static final Long ARTIKEL_ID_VORHANDEN_1 = Long.valueOf(501);
	 

	 
	@Test
	public void findeBestellungNachIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeBestellungNachIdVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				                         .get(BESTELLUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(bestellungId.longValue()));
			assertThat(jsonObject.getString("kunde"), is(notNullValue()));
		}

		LOGGER.debugf("ENDE Test findeBestellungNachIdVorhanden");
	}
	
	 
	@Test
	public void findeBestellungNachIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeBestellungNachIdNichtVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				                         .get(BESTELLUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeBestellungNachIdNichtVorhanden");
	}
	
		
	@Ignore 
	@Test
	public void findeKundeNachBestellungIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeKundeNachBestellungIdVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH + "/kunde");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getString("bestellungen"),
					   endsWith("/kunden/" + jsonObject.getInt("id") + "/bestellungen"));
		}

		LOGGER.debugf("ENDE Test findeKundeNachBestellungIdVorhanden");
	}
	
	 
	@Test
	public void findeKundeNachBestellungIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeKundeNachBestellungIdNichtVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter("bestellungId", bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH + "/kunde");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeKundeNachBestellungIdNichtVorhanden");
	}
	
	@Ignore 
	@Test
	public void findeLieferungenNachBestellungIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeLieferungenNachBestellungIdVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH + "/lieferungen");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();			 
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for(JsonObject jsonObject : jsonObjectList)
				assertThat(jsonObject.getJsonNumber("id").longValue(), is(bestellungId.longValue()));
				final JsonObject jsonObject = jsonReader.readObject();
				assertThat(jsonObject.getString("bestellungen"), is(notNullValue()));				
				 
		}

		LOGGER.debugf("ENDE Test findeLieferungenNachBestellungIdVorhanden");
	}

	 
	@Test
	public void findeLieferungenNachBestellungIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeLieferungenNachBestellungIdNichtVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH + "/lieferungen");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeLieferungenNachBestellungIdNichtVorhanden");
	}
	
	@Ignore
	@Test
	public void createBestellung() {
		LOGGER.debugf("BEGINN Test createBestellung");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikelId1 = ARTIKEL_ID_VORHANDEN_1;		 
		 
		
		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				                      .add("kundeUri", KUNDEN_URI + "/" + kundeId)
				                      .add("bestellpositionen", getJsonBuilderFactory().createArrayBuilder()
				            		                            .add(getJsonBuilderFactory().createObjectBuilder()
				            		                                 .add("artikelUri", ARTIKEL_URI + "/" + artikelId1)
				            		                                 .add("anzahl", 1)))
				                      .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())				                          
				                         .post(BESTELLUNGEN_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE Test createBestellung");
	}
}
