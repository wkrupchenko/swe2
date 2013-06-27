package de.shop.service;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static de.shop.ShopApp.jsonReaderFactory;
import static de.shop.ui.main.Prefs.username;
import static de.shop.util.Konstanten.ARTIKEL_PATH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.JsonNumber;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import android.text.TextUtils;
import android.util.Log;
import de.shop.data.kunde.Kunde;
import de.shop.data.artikel.Artikel;
import de.shop.data.artikel.Artikelgruppe;
import de.shop.util.InternalShopError;
import de.shop.R;
import de.shop.ShopApp;

final class Mock {
	
private static final String LOG_TAG = Mock.class.getSimpleName();
	
	private static String read(int dateinameId) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(ShopApp.open(dateinameId)));
    	final StringBuilder sb = new StringBuilder();
    	try {
    		for (;;) {
				final String line = reader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
		}
    	catch (IOException e) {
    		throw new InternalShopError(e.getMessage(), e);
		}
    	finally {
    		if (reader != null) {
    			try {
					reader.close();
				}
    			catch (IOException e) {}
    		}
    	}
    	
    	final String jsonStr = sb.toString();
    	Log.v(LOG_TAG, "jsonStr = " + jsonStr);
		return jsonStr;
	}
	
	static HttpResponse<Artikel> sucheArtikelNachId(Long id) {
    	if (id <= 0 || id >= 1000) {
    		return new HttpResponse<Artikel>(HTTP_NOT_FOUND, "Kein Artikel gefunden mit ID " + id);
    	}
    	
    	int dateinameId = R.raw.mock_artikel;
    	
    	final String jsonStr = read(dateinameId);
    	JsonReader jsonReader = null;
    	JsonObject jsonObject;
    	try {
    		jsonReader = jsonReaderFactory.createReader(new StringReader(jsonStr));
    		jsonObject = jsonReader.readObject();
    	}
    	finally {
    		if (jsonReader != null) {
    			jsonReader.close();
    		}
    	}
    	final Artikel artikel = new Artikel();

         artikel.fromJsonObject(jsonObject);
         artikel.id = id;

         final HttpResponse<Artikel> result = new HttpResponse<Artikel>(HTTP_OK, jsonObject.toString(), artikel);
         return result;
	}
	
    static List<Long> sucheKundeIdsByPrefix(String kundeIdPrefix) {
		int dateinameId = -1;
		/*
    	if ("1".equals(kundeIdPrefix)) {
    		dateinameId = R.raw.mock_ids_1;
    	}
    	else if ("10".equals(kundeIdPrefix)) {
    		dateinameId = R.raw.mock_ids_10;
    	}
    	else if ("11".equals(kundeIdPrefix)) {
    		dateinameId = R.raw.mock_ids_11;
    	}
    	else if ("2".equals(kundeIdPrefix)) {
    		dateinameId = R.raw.mock_ids_2;
    	}
    	else if ("20".equals(kundeIdPrefix)) {
    		dateinameId = R.raw.mock_ids_20;
    	}
    	else {
    		return Collections.emptyList();
    	}
    	*/
    	final String jsonStr = read(dateinameId);
		JsonReader jsonReader = null;
    	JsonArray jsonArray;
    	try {
    		jsonReader = jsonReaderFactory.createReader(new StringReader(jsonStr));
    		jsonArray = jsonReader.readArray();
    	}
    	finally {
    		if (jsonReader != null) {
    			jsonReader.close();
    		}
    	}
    	
    	final List<Long> result = new ArrayList<Long>(jsonArray.size());
    	final List<JsonNumber> jsonNumberList = jsonArray.getValuesAs(JsonNumber.class);
	    for (JsonNumber jsonNumber : jsonNumberList) {
	    	final Long id = Long.valueOf(jsonNumber.longValue());
	    	result.add(id);
    	}
    	
    	Log.d(LOG_TAG, "ids= " + result.toString());
    	
    	return result;
    }
    
	static HttpResponse<Artikel> sucheArtikelNachBezeichnung(String bezeichnung) {
    	if (bezeichnung.equals("Nix")) {
    		return new HttpResponse<Artikel>(HTTP_NOT_FOUND, "Kein Artikel gefunden mit Bezeichnung " + bezeichnung);
    	}
    	
    	int dateinameId = R.raw.mock_artikel;
    	
    	final String jsonStr = read(dateinameId);
    	JsonReader jsonReader = null;
    	JsonObject jsonObject;
    	try {
    		jsonReader = jsonReaderFactory.createReader(new StringReader(jsonStr));
    		jsonObject = jsonReader.readObject();
    	}
    	finally {
    		if (jsonReader != null) {
    			jsonReader.close();
    		}
    	}
    	final Artikel artikel = new Artikel();

         artikel.fromJsonObject(jsonObject);
         artikel.bezeichnung = bezeichnung;

         final HttpResponse<Artikel> result = new HttpResponse<Artikel>(HTTP_OK, jsonObject.toString(), artikel);
         return result;
	}
	
	static HttpResponse<Artikelgruppe> sucheArtikelgruppeNachArtikel(Long id) {
    	if (id <= 0 || id >= 1000) {
    		return new HttpResponse<Artikelgruppe>(HTTP_NOT_FOUND, "Keine Artikelgruppe gefunden mit Artikel ID " + id);
    	}
    	
    	int dateinameId = R.raw.mock_artikelgruppe;
    	
    	final String jsonStr = read(dateinameId);
    	JsonReader jsonReader = null;
    	JsonObject jsonObject;
    	try {
    		jsonReader = jsonReaderFactory.createReader(new StringReader(jsonStr));
    		jsonObject = jsonReader.readObject();
    	}
    	finally {
    		if (jsonReader != null) {
    			jsonReader.close();
    		}
    	}
    	final Artikelgruppe artikelgruppe = new Artikelgruppe();

         artikelgruppe.fromJsonObject(jsonObject);
         artikelgruppe.id = id;

         final HttpResponse<Artikelgruppe> result = new HttpResponse<Artikelgruppe>(HTTP_OK, jsonObject.toString(), artikelgruppe);
         return result;
	}
	
	static HttpResponse<Artikel> sucheArtikelNachArtikelgruppe(Long id) {
    	if (id < 0 || id > 10000) {
    		return new HttpResponse<Artikel>(HTTP_NOT_FOUND, "Kein Artikel gefunden zur Artikelgruppe " + id);
    	}
    	
    	int dateinameId = R.raw.mock_artikel;
    	
    	final String jsonStr = read(dateinameId);
    	JsonReader jsonReader = null;
    	JsonObject jsonObject;
    	try {
    		jsonReader = jsonReaderFactory.createReader(new StringReader(jsonStr));
    		jsonObject = jsonReader.readObject();
    	}
    	finally {
    		if (jsonReader != null) {
    			jsonReader.close();
    		}
    	}
    	final Artikel artikel = new Artikel();

         artikel.fromJsonObject(jsonObject);
         artikel.id = id;

         final HttpResponse<Artikel> result = new HttpResponse<Artikel>(HTTP_OK, jsonObject.toString(), artikel);
         return result;
	}
	
	static HttpResponse<Void> deleteArtikel(Long artikelId) {
    	Log.d(LOG_TAG, "deleteArtikel: " + artikelId);
    	return new HttpResponse<Void>(HTTP_NO_CONTENT, null);
    }
	
	static HttpResponse<Kunde> sucheKundeById(Long id) {
    	if (id <= 0 || id >= 1000) {
    		return new HttpResponse<Kunde>(HTTP_NOT_FOUND, "Kein Kunde gefunden mit ID " + id);
    	}
    	
    	int dateinameId = 0;    	    	
    	final String jsonStr = read(dateinameId);
    	JsonReader jsonReader = null;
    	JsonObject jsonObject;
    	try {
    		jsonReader = jsonReaderFactory.createReader(new StringReader(jsonStr));
    		jsonObject = jsonReader.readObject();
    	}
    	finally {
    		if (jsonReader != null) {
    			jsonReader.close();
    		}
    	}
    	
    	final Kunde kunde = new Kunde();
    	kunde.fromJsonObject(jsonObject);
    	kunde.id = id;
		
    	final HttpResponse<Kunde> result = new HttpResponse<Kunde>(HTTP_OK, jsonObject.toString(), kunde);
    	return result;
	}
	
	 static HttpResponse<Artikel> updateArtikel(Artikel artikel) {
	    	Log.d(LOG_TAG, "updateArtikel: " + artikel);
	    	
	    	if (TextUtils.isEmpty(username)) {
	    		return new HttpResponse<Artikel>(HTTP_UNAUTHORIZED, null);
	    	}
	    	
	    	if ("x".equals(username)) {
	    		return new HttpResponse<Artikel>(HTTP_FORBIDDEN, null);
	    	}
	    	
	    	Log.d(LOG_TAG, "updateArtikel: " + artikel.toJsonObject());
	    	return new HttpResponse<Artikel>(HTTP_NO_CONTENT, null, artikel);
	    }
	 
	 static HttpResponse<Artikel> createArtikel(Artikel artikel) {
	    	artikel.id = Long.valueOf(artikel.bezeichnung.length());  // Anzahl der Buchstaben der Bezeichnung als emulierte neue ID
	    	Log.d(LOG_TAG, "createArtikel: " + artikel);
	    	Log.d(LOG_TAG, "createArtikel: " + artikel.toJsonObject());
	    	final HttpResponse<Artikel> result = new HttpResponse<Artikel>(HTTP_CREATED, ARTIKEL_PATH + "/1", artikel);
	    	return result;
	    }
	
	private Mock() {}
}
