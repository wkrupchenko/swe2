package de.shop.service;

import static de.shop.ui.main.Prefs.mock;
import static android.app.ProgressDialog.STYLE_SPINNER;
import static de.shop.util.Konstanten.ARTIKEL_PATH;
import static de.shop.ui.main.Prefs.timeout;
import static de.shop.util.Konstanten.LOCALHOST;
import static de.shop.util.Konstanten.LOCALHOST_EMULATOR;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import de.shop.R;
import de.shop.data.artikel.Artikel;
import de.shop.util.InternalShopError;

public class ArtikelService extends Service {
	private static final String LOG_TAG = ArtikelService.class.getSimpleName();
	private static final String TYPE = "type";
	private static final Map<String, Class<? extends Artikel>> CLASS_MAP;
	
	private final ArtikelServiceBinder binder = new ArtikelServiceBinder();
	
	static {
		CLASS_MAP = new HashMap<String, Class<? extends Artikel>>(1, 1);
		CLASS_MAP.put("A", Artikel.class);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class ArtikelServiceBinder extends Binder {
		
		public ArtikelService getService() {
			return ArtikelService.this;
		}
		
		private ProgressDialog progressDialog;
		private ProgressDialog showProgressDialog(Context ctx) {
			progressDialog = new ProgressDialog(ctx);
			progressDialog.setProgressStyle(STYLE_SPINNER);  // Kreis (oder horizontale Linie)
			progressDialog.setMessage(getString(R.string.s_bitte_warten));
			progressDialog.setCancelable(true);      // Abbruch durch Zuruecktaste
			progressDialog.setIndeterminate(true);   // Unbekannte Anzahl an Bytes werden vom Web Service geliefert
			progressDialog.show();
			return progressDialog;
		}
		
		public HttpResponse<Artikel> sucheArtikelNachId(Long id, final Context ctx) {
			
			final AsyncTask<Long, Void, HttpResponse<Artikel>> sucheArtikelNachIdTask = new AsyncTask<Long, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(Long... ids) {
					final Long id = ids[0];
		    		final String path = ARTIKEL_PATH + "/" + id;
		    		Log.v(LOG_TAG, "path = " + path);
		    		final HttpResponse<Artikel> result = mock
		    				                                   ? Mock.sucheArtikelNachId(id)
		    				                                   : WebServiceClient.getJsonSingle(path, TYPE, CLASS_MAP);

					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};

    		sucheArtikelNachIdTask.execute(id);
    		HttpResponse<Artikel> result = null;
	    	try {
	    		result = sucheArtikelNachIdTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
	    	
    		if (result.responseCode != HTTP_OK) {
	    		return result;
		    }
    		
    		setArtikelgruppeUri(result.resultObject);
		    return result;
		}
		
		private void setArtikelgruppeUri(Artikel artikel) {
	    	// URL der Artikelgruppe fuer Emulator anpassen
	    	final String artikelgruppeUri = artikel.artikelgruppeUri;
	    	if (!TextUtils.isEmpty(artikelgruppeUri)) {
			    artikel.artikelgruppeUri = artikelgruppeUri.replace(LOCALHOST, LOCALHOST_EMULATOR);
	    	}
		}
		/*public Artikel sucheArtikelNachId(Long id) {
			
			final AsyncTask<Long, Void, Artikel> sucheArtikelNachIdTask = new AsyncTask<Long, Void, Artikel>() {
				@Override
	    		protected void onPreExecute() {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread starten ...");
				}
				
				@Override
				// Neuer Thread (hier: Emulation des REST-Aufrufs), damit der UI-Thread nicht blockiert wird
				protected Artikel doInBackground(Long... ids) {
					final Long artikelId = ids[0];
			    	Artikel artikel;
			    	if (mock) {
			    		artikel = ArtikelMock.sucheArtikelNachId(artikelId);
			    	}
			    	else {
			    		Log.e(LOG_TAG, "Suche nach Artikelnummer ist nicht implementiert");
			    		return null;
			    	}
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + artikel);
					return artikel;
				}
				
				@Override
	    		protected void onPostExecute(Artikel artikel) {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread beenden ...");
	    		}
			};

			sucheArtikelNachIdTask.execute(id);
	    	Artikel artikel = null;
	    	try {
	    		artikel = sucheArtikelNachIdTask.get(3L, TimeUnit.SECONDS);
			}
	    	catch (Exception e) {
	    		Log.e(LOG_TAG, e.getMessage(), e);
			}
			return artikel;
		}*/
	}
}
