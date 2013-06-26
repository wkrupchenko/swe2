package de.shop.service;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static de.shop.ShopApp.jsonReaderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.json.JsonObject;
import javax.json.JsonReader;

import android.util.Log;
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
	
	private Mock() {}
}
