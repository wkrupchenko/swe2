package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestKonstanten.ACCEPT;
import static de.shop.util.TestKonstanten.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestKonstanten.KUNDEN_ID_PATH;
import static de.shop.util.TestKonstanten.KUNDEN_NACHNAME_QUERY_PARAM;
import static de.shop.util.TestKonstanten.KUNDEN_PATH;
import static de.shop.util.TestKonstanten.LOCATION;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static de.shop.util.TestKonstanten.KUNDEN_ID_PREFIX_PATH_PARAM;
import static de.shop.util.TestKonstanten.KUNDEN_ID_PREFIX_PATH;
import static de.shop.util.TestKonstanten.KUNDEN_NACHNAME_PREFIX_PATH_PARAM;
import static de.shop.util.TestKonstanten.KUNDEN_NACHNAME_PREFIX_PATH;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractResourceTest;
import de.shop.kundenverwaltung.domain.FamilienstandTyp;
import de.shop.kundenverwaltung.domain.GeschlechtTyp;
 
@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class KundeResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long KUNDE_ID_UPDATE = Long.valueOf(100);
	private static final Long KUNDE_ID_DELETE = Long.valueOf(105);
	private static final Long KUNDE_ID_DELETE_MIT_BESTELLUNGEN = Long.valueOf(103);
	private static final Long KUNDE_ID_DELETE_2 = Long.valueOf(102);
	private static final String NACHNAME_VORHANDEN = "Musterfrau";
	private static final String NACHNAME_NICHT_VORHANDEN = "Falschername";
	private static final String NEUER_NACHNAME = "Nachnameneu";
	private static final String NEUER_NACHNAME_INVALID = "!";
	private static final String NEUER_VORNAME = "Vorname";
	private static final String NEUE_EMAIL = NEUER_NACHNAME + "@test.de";
	private static final String NEUE_EMAIL_INVALID = "falsch@falsch";
	private static final BigDecimal NEUER_RABATT = new BigDecimal("0.15");
	private static final BigDecimal NEUER_UMSATZ = new BigDecimal("10000");
	private static final String NEU_FAMILIENSTAND = FamilienstandTyp.LEDIG.toString();
	private static final String NEU_GESCHLECHT = GeschlechtTyp.MAENNLICH.toString();
	private static final String NEU_PASSWORT = "neuespwd";
	private static final Boolean NEU_NEWSLETTER = true;
	private static final String NEU_SEIT = "2000-01-31";
	private static final String NEUE_PLZ = "76133";
	private static final String NEUER_ORT = "Karlsruhe";
	private static final String NEUE_STRASSE = "Testweg";
	private static final String NEUE_HAUSNR = "1";
	private static final Long KUNDE_ID_PREFIX = Long.valueOf(1);
	private static final String KUNDE_NACHNAME_PREFIX = "M";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	 
	@Test
	public void findeKundeNachIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeKundeNachId");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(kundeId.longValue()));
		}
		
		LOGGER.debugf("ENDE Test findeKundeNachId");
	}
	
	 
	@Test
	public void findeKundeNachIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeKundeNachIdNichtVorhanden");
		
		// Given
		final Long kundeId = KUNDE_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.debugf("ENDE Test findeKundeNachIdNichtVorhanden");
	}
	
	 
	@Test
	public void findeKundenNachNachnameVorhanden() {
		LOGGER.debugf("BEGINN Test findeKundenNachNachnameVorhanden");
		
		// Given
		final String nachname = NACHNAME_VORHANDEN;

		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
                                         .get(KUNDEN_PATH);
		
		// Then
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
	    	assertThat(jsonArray.size() > 0, is(true));
	    	
	    	final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
	    	for (JsonObject jsonObject : jsonObjectList) {
	    		assertThat(jsonObject.getString("nachname"), is(nachname));
	    	}
		}

		LOGGER.debugf("ENDE Test findeKundenNachNachnameVorhanden");
	}
	
	 
	@Test
	public void findeKundenNachNachnameNichtVorhanden() {
		LOGGER.debugf("BEGINN _FINDE_KUNDEN_NACH_NACHNAMEN_NICHT_VORHANDEN");
		
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
                                         .get(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.debugf("ENDE");
	}
	
	@Test
	public void findeIdsNachPrefix() {
		LOGGER.debugf("BEGINN Test findeIdsNachPrefix");
		// Given
		final Long prefix = KUNDE_ID_PREFIX;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										.pathParam(KUNDEN_ID_PREFIX_PATH_PARAM, prefix)
										.get(KUNDEN_ID_PREFIX_PATH);
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();			 
			assertThat(jsonArray.size() > 0, is(true));
			for (int i = 0; i < jsonArray.size(); ++i) {
				final String id = String.valueOf(jsonArray.getInt(i));
				assertThat(id.startsWith(prefix.toString()), is(true));
			}
		}
		LOGGER.debugf("ENDE Test findeIdsNachPrefix");
	}
	
	@Test
	public void findeNachnamenNachPrefix() {
		LOGGER.debugf("BEGINN Test findeNachnameNachPrefix");
		// Given
		final String prefix = KUNDE_NACHNAME_PREFIX;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										.pathParam(KUNDEN_NACHNAME_PREFIX_PATH_PARAM, prefix)
										.get(KUNDEN_NACHNAME_PREFIX_PATH);
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();			 
			assertThat(jsonArray.size() > 0, is(true));
			for (int i = 0; i < jsonArray.size(); ++i) {
				final String id = jsonArray.getString(i);
				assertThat(id.startsWith(prefix), is(true));
			}
		}
		LOGGER.debugf("ENDE Test findeNachnameNachPrefix");
	}

	@Test
	public void findeBestellungenNachKundeIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeBestellungenNachKundeIdVorhanden");
		
		// Given
		final String kundeId = KUNDE_ID_VORHANDEN.toString();
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
                .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                .get(KUNDEN_ID_PATH + "/bestellungen");

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));

		// Liste mit den Bestellungen des Kunden erzeugen
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));
			
			final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for (JsonObject jsonObject : jsonObjectList) {
				assertThat(jsonObject.getString("kunde").endsWith(kundeId), is(true));
			}
		}

		LOGGER.debugf("ENDE Test findeBestellungenNachKundeIdVorhanden");
	}

	@Test
	public void findeBestellungenNachKundeIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeBestellungenNachKundeIdNichtVorhanden");
		
		// Given
		final String kundeId = KUNDE_ID_NICHT_VORHANDEN.toString();
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
                .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                .get(KUNDEN_ID_PATH + "/bestellungen");

		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.debugf("ENDE Test findeBestellungenNachKundeIdNichtVorhanden");
	}
	
	@Test
	public void findeBestellungenIdsNachKundeIdVorhanden() {
		LOGGER.debugf("BEGINN Test findeBestellungenIdsNachKundeIdVorhanden");
		
		// Given
		final long kundeId = KUNDE_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
                .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                .get(KUNDEN_ID_PATH + "/bestellungenIds");

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));

		// Liste mit den Bestellungen des Kunden erzeugen
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));
		}

		LOGGER.debugf("ENDE Test findeBestellungenIdsNachKundeIdVorhanden");
	}
	
	@Test
	public void findeBestellungenIdsNachKundeIdNichtVorhanden() {
		LOGGER.debugf("BEGINN Test findeBestellungenIdsNachKundeIdNichtVorhanden");
		
		// Given
		final long kundeId = KUNDE_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
                .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                .get(KUNDEN_ID_PATH + "/bestellungen");

		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.debugf("ENDE Test findeBestellungenIdsNachKundeIdNichtVorhanden");
	}

	 
	@Test
	public void createKunde() {
		LOGGER.debugf("BEGINN Test createKunde");
		
		// Given
		final String nachname = NEUER_NACHNAME;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL;
		final BigDecimal rabatt = NEUER_RABATT;
		final BigDecimal umsatz = NEUER_UMSATZ;
		final String seit = NEU_SEIT;
		final String familienstand = NEU_FAMILIENSTAND;
		final String geschlecht = NEU_GESCHLECHT;
		final Boolean newsletter = NEU_NEWSLETTER;
		final String passwort = NEU_PASSWORT;
		final String plz = NEUE_PLZ;
		final String ort = NEUER_ORT;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		             		          .add("art", Kunde.PRIVATKUNDE)
		             		          .add("email", email)
		             		          .add("familienstand", familienstand)
		             		          .add("geschlecht", geschlecht)
		             		          .add("nachname", nachname)
		             		          .add("vorname", vorname)
		             		          .add("newsletter", newsletter)
		             		          .add("passwort", passwort)
		             		          .add("rabatt", rabatt)
		             		          .add("seit", seit)
		             		          .add("umsatz", umsatz)
		             		          .add("adresse", getJsonBuilderFactory().createObjectBuilder()
		             		        		  		  .add("strasse", strasse)
		             		        		  		  .add("hausnr", hausnr)
		                    		                  .add("plz", plz)
		                    		                  .add("ort", ort)
		                    		                  .build())		                               
		                              .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString()) 
				                         .auth()
				                         .basic(username, password)
                                         .post(KUNDEN_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE Test createKunde");
	}
	
	 
	@Test
	public void createKundeFalschesPassword() {
		LOGGER.debugf("BEGINN Test createKundeFalschesPassword");
		
		// Given
		final String username = USERNAME;
		final String password = PASSWORD_FALSCH;
		final String nachname = NEUER_NACHNAME;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
            		                  .add("type", Kunde.PRIVATKUNDE)
            		                  .add("nachname", nachname)
            		                  .build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
                                         .auth()
                                         .basic(username, password)
                                         .post(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
		
		LOGGER.debugf("ENDE Test createKundeFalschesPassword");
	}
	
	@Test
	public void createKundeInvalid() {
		LOGGER.debugf("BEGINN Test createKundeInvalid");
		
		// Given
		final String nachname = NEUER_NACHNAME_INVALID;
		final String vorname = NEUER_VORNAME;
		final BigDecimal rabatt = NEUER_RABATT;
		final BigDecimal umsatz = NEUER_UMSATZ;
		final String seit = NEU_SEIT;
		final String familienstand = NEU_FAMILIENSTAND;
		final String geschlecht = NEU_GESCHLECHT;
		final Boolean newsletter = NEU_NEWSLETTER;
		final String passwort = NEU_PASSWORT;
		final String email = NEUE_EMAIL_INVALID;
		final String username = USERNAME;
		final String password = PASSWORD;

		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
									  .add("art", Kunde.PRIVATKUNDE)
							          .add("email", email)
							          .add("familienstand", familienstand)
							          .add("geschlecht", geschlecht)
							          .add("nachname", nachname)
							          .add("vorname", vorname)
							          .add("newsletter", newsletter)
							          .add("passwort", passwort)
							          .add("rabatt", rabatt)
							          .add("seit", seit)
							          .add("umsatz", umsatz)
   		                              .addNull("adresse")
   		                              .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
                                         .auth()
                                         .basic(username, password)
                                         .post(KUNDEN_PATH);
	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		assertThat(response.asString().isEmpty(), is(false));
		
		LOGGER.debugf("ENDE Test createKundeInvalid");
	}
	
	 
	@Test
	public void updateKunde() {
		
		LOGGER.debugf("BEGINN Test updateKunde");
		
		// Given
		final Long kundeId = KUNDE_ID_UPDATE;
		final String neuerNachname = NEUER_NACHNAME;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                   .get(KUNDEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(kundeId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("nachname".equals(k)) {
    			job.add("nachname", neuerNachname);
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
                          .put(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		LOGGER.debugf("ENDE Test updateKunde");
   	}

	 
	@Test
	public void deleteKunde() {
		
		LOGGER.debugf("BEGINN Test deleteKunde");
		
		// Given
		final Long kundeId = KUNDE_ID_DELETE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		final Response response = given().auth()
                                         .basic(username, password)
                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .delete(KUNDEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.debugf("ENDE Test deleteKunde");
	}
	
	
	@Test
	public void deleteKundeMitBestellung() {
		
		LOGGER.debugf("BEGINN Test deleteKundeMitBestellung");
		
		// Given
		final Long kundeId = KUNDE_ID_DELETE_MIT_BESTELLUNGEN;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		final Response response = given().auth()
                                         .basic(username, password)
                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .delete(KUNDEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		final String errorMsg = response.getBody().asString();
		assertThat(errorMsg, containsString("Kunde mit ID=" + kundeId + " kann nicht geloescht werden:"));

		LOGGER.debugf("ENDE deleteKundeMitBestellung");
	}
	
	@Test
	public void deleteKundeFehlendeBerechtigung() {
		LOGGER.debugf("BEGINN Test deleteKundeFehlendeBerechtigung");
	
		// Given
		final String username = USERNAME_KUNDE;
		final String password = PASSWORD_KUNDE;
		final Long kundeId = KUNDE_ID_DELETE_2;
		
		// When
		final Response response = given().auth()
                                         .basic(username, password)
                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .delete(KUNDEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), anyOf(is(HTTP_FORBIDDEN), is(HTTP_NOT_FOUND)));
		
		LOGGER.debugf("ENDE Test deleteKundeFehlendeBerechtigung");
	}
}
