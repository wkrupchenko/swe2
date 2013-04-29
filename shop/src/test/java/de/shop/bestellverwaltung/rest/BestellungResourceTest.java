package de.shop.bestellverwaltung.rest;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestKonstanten.ACCEPT;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_PATH;
import static de.shop.util.TestKonstanten.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.ARTIKEL_PATH;
import static de.shop.util.TestKonstanten.ARTIKEL_URI;
import static de.shop.util.TestKonstanten.BESTELLUNGEN_ID_PATH;
import static de.shop.util.TestKonstanten.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.BESTELLUNGEN_PATH;
import static de.shop.util.TestKonstanten.KUNDEN_URI;
import static de.shop.util.TestKonstanten.LOCATION;
import static de.shop.util.TestKonstanten.LIEFERUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.LIEFERUNGEN_ID_PATH;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
	private static final Long BESTELLUNG_ID_LOESCHEN = Long.valueOf(301);
	private static final Long BESTELLUNG_ID_NICHT_VORHANDEN = Long.valueOf(999);
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);
	private static final Long ARTIKEL_ID_VORHANDEN_1 = Long.valueOf(501);
	private static final Long LIEFERUNG_ID_VORHANDEN = Long.valueOf(700);
	private static final Long LIEFERUNG_ID_NICHT_VORHANDEN = Long.valueOf(999);
	private static final Long BESTELLUNG_ID_UPDATE = Long.valueOf(300);
	private static final String BESTELLUNG_NEUE_BEZEICHNUNG = "Bestellung geändert";
	

	 
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
		
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
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
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH + "/kunde");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeKundeNachBestellungIdNichtVorhanden");
	}
	
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
		
		// Liste mit den Lieferungen zur gegebenen Bestellung erzeugen
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();			 
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			
			// Jede Lieferung einzeln durchgehen -> je Lieferung eine neue HTTP Anfrage an die URL der Bestellungen
			for(JsonObject jsonObject : jsonObjectList) {
				final Response temp = get(jsonObject.getJsonString("bestellungen").toString());
				assertThat(temp.getStatusCode(), is(HTTP_OK));
			
				// Eine Liste mit den Bestellungen zur Lieferung erzeugen
				try (final JsonReader jsonReaderB =
			              getJsonReaderFactory().createReader(new StringReader(temp.asString()))) {
					JsonArray tempB = jsonReaderB.readArray();			 
					assertThat(tempB.size() > 0, is(true));
					List<JsonObject> bestellungen = tempB.getValuesAs(JsonObject.class);
				
					// Ids der Bestellungen in eine Liste packen
					List<Long> bestellungenIds = new ArrayList<Long>();
					for( JsonObject bestellung : bestellungen)
						bestellungenIds.add(bestellung.getJsonNumber("id").longValue());
				
					// Prüfen ob die Bestellung vorhanden ist
					assertThat(bestellungenIds.contains(bestellungId.longValue()), is(true));
				}
			}
				 
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
	
	@Test
	public void findeAlleOffenenBestellungen() {
		LOGGER.debugf("BEGINN Test findeAlleOffenenBestellungen");
		
		// Given
		final Boolean offenAbgeschlossen = true;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										.queryParam("offenAbgeschlossen", offenAbgeschlossen)
										.get(BESTELLUNGEN_PATH);
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		// Liste mit den Bestellungen erzeugen
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();			 
			assertThat(jsonArray.size() > 0, is(true));
			List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			
			for(JsonObject jsonObject : jsonObjectList)
				assertThat(jsonObject.getBoolean("offenAbgeschlossen"), is(offenAbgeschlossen));
		}
		LOGGER.debugf("Ende Test findeAlleOffenenBestellungen");
	}
	
	@Test
	public void findeLieferungNachIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeLieferungNachIdVorhanden");
		// Given
		final Long id = LIEFERUNG_ID_VORHANDEN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
									.pathParam(LIEFERUNGEN_ID_PATH_PARAM, id)
									.get(LIEFERUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (final JsonReader jsonReader =
	              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(id.longValue()));
			assertThat(jsonObject.getString("bestellungen"), is(notNullValue()));
		}
		LOGGER.debugf("ENDE Test findeLieferungNachIdVorhanden");
	}
	
	@Test
	public void findeLieferungNachIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeLieferungNachIdNichtVorhanden");
		// Given
		final Long id = LIEFERUNG_ID_NICHT_VORHANDEN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
									.pathParam(LIEFERUNGEN_ID_PATH_PARAM, id)
									.get(LIEFERUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeLieferungNachIdNichtVorhanden");
	}
	
	 
	@Test
	public void findeBestellungNachLieferungenVorhanden() {
		LOGGER.debugf("BEGINN Test BestellungNachLieferungenVorhanden");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                         .get(LIEFERUNGEN_ID_PATH + "/bestellungen");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		LOGGER.debugf("ENDE Test BestellungNachLieferungenVorhanden");
		
		try (final JsonReader jsonReader =
	      getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
		  JsonArray jsonArray = jsonReader.readArray();			 
          assertThat(jsonArray.size() > 0, is(true));
          List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);

          	   // Jede Bestellung einzeln durchgehen -> je Bestellung eine neue HTTP Anfrage an die URL der Lieferungen
	          for(JsonObject jsonObject : jsonObjectList) {
			      final Response temp = get(jsonObject.getJsonString("lieferungen").toString());
			      assertThat(temp.getStatusCode(), is(HTTP_OK));

			          // Eine Liste mit den Lieferungen zur Bestellung erzeugen
			          try (final JsonReader jsonReaderB =
				          getJsonReaderFactory().createReader(new StringReader(temp.asString()))) {
						  JsonArray tempB = jsonReaderB.readArray();			 
						  assertThat(tempB.size() > 0, is(true));
						  List<JsonObject> lieferungen = tempB.getValuesAs(JsonObject.class);
					
						  // Ids der Lieferungen in eine Liste packen
						  List<Long> lieferungenIds = new ArrayList<Long>();
							for( JsonObject lieferung : lieferungen)
								lieferungenIds.add(lieferung.getJsonNumber("id").longValue());
						
								// Prüfen ob die Bestellung vorhanden ist
								assertThat(lieferungenIds.contains(lieferungId.longValue()), is(true));
	                        }
	                  }
		       }
		          LOGGER.debugf("ENDE Test BestellungNachLieferungenVorhanden");
	      }
                       
	@Test
	public void findeBestellungNachLieferungenNichtVorhanden() {
		LOGGER.debugf("BEGINN Test BestellungNachLieferungenVorhanden");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                         .get(LIEFERUNGEN_ID_PATH + "/bestellungen");
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test BestellungNachLieferungenNichtVorhanden");
		 
	}
	
	 
	@Ignore
	@Test
	public void createBestellung() {
		// TODO
		LOGGER.debugf("BEGINN Test createBestellung");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikelId1 = ARTIKEL_ID_VORHANDEN_1;	
		final String username = USERNAME;
		final String password = PASSWORD;
		 
		
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
				                         .auth()
				                         .basic(username, password)
				                         .post(BESTELLUNGEN_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE Test createBestellung");
	}
	
	@Ignore
	@Test
	public void updateBestellung() {
		// TODO
		LOGGER.debugf("BEGINN Test updateBestellung");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_UPDATE;
		final String neueBezeichnung = BESTELLUNG_NEUE_BEZEICHNUNG;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				                   .auth()
				                   .basic(username, password)
                                   .get(BESTELLUNGEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(bestellungId.longValue()));
    	
    	
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
                          .put(BESTELLUNGEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	
		LOGGER.debugf("ENDE Test updateBestellung");
	}
	 
	@Test
	public void deleteBestellung() {
		LOGGER.debugf("BEGINN Test deleteBestellung");
		
		// GIVEN
		final Long bestellungId = BESTELLUNG_ID_LOESCHEN;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// WHEN
		final Response response = given().pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
										 .auth()
										 .basic(username,  password).delete(BESTELLUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		final String errorMsg = response.asString();
		assertThat(errorMsg, startsWith("Bestellung mit ID=" + bestellungId + " kann nicht geloescht werden:"));
		assertThat(errorMsg, endsWith(" Lieferung(en)"));
		
		LOGGER.debugf("ENDE Test deleteBestellung");
	}	 
}