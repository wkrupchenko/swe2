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
import de.shop.data.artikel.Artikelgruppe;
import de.shop.util.InternalShopError;

public class ArtikelService extends Service {
	private static final String LOG_TAG = ArtikelService.class.getSimpleName();
	
	private final ArtikelServiceBinder binder = new ArtikelServiceBinder();
	

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
		    				                                   : WebServiceClient.getJsonSingle(path, Artikel.class);

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
		
		public HttpResponse<Artikel> sucheArtikelNachBezeichnung(String bezeichnung, final Context ctx) {
			
			final AsyncTask<String, Void, HttpResponse<Artikel>> sucheArtikelNachBezeichnungTask = new AsyncTask<String, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(String... bzs) {
					final String bezeichnung = bzs[0];
		    		final String path = ARTIKEL_PATH + "?bezeichnung=" + bezeichnung;
		    		Log.v(LOG_TAG, "path = " + path);
		    		final HttpResponse<Artikel> result = mock
		    				                                   ? Mock.sucheArtikelNachBezeichnung(bezeichnung)
		    				                                   : WebServiceClient.getJsonList(path, Artikel.class);

					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};

    		sucheArtikelNachBezeichnungTask.execute(bezeichnung);
    		HttpResponse<Artikel> result = null;
	    	try {
	    		result = sucheArtikelNachBezeichnungTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
	    	
    		if (result.responseCode != HTTP_OK) {
	    		return result;
		    }
    		
    		final ArrayList<Artikel> artikel = result.resultList;
	    	// URLs fuer Emulator anpassen
	    	for (Artikel a : artikel) {
	    		setArtikelgruppeUri(a);
	    	}
			return result;
		}
		
		private void setArtikelgruppeUri(Artikel artikel) {
	    	// URL der Artikelgruppe fuer Emulator anpassen
	    	final String artikelgruppeUri = artikel.artikelgruppeUri;
	    	if (!TextUtils.isEmpty(artikelgruppeUri)) {
			    artikel.artikelgruppeUri = artikelgruppeUri.replace(LOCALHOST, LOCALHOST_EMULATOR);
	    	}
		}
		
		public HttpResponse<Artikelgruppe> sucheArtikelgruppeNachArtikel(Long id, final Context ctx) {
			
			final AsyncTask<Long, Void, HttpResponse<Artikelgruppe>> sucheArtikelgruppeNachArtikelTask = new AsyncTask<Long, Void, HttpResponse<Artikelgruppe>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikelgruppe> doInBackground(Long... ids) {
					final Long id = ids[0];
		    		final String path = ARTIKEL_PATH + "/" + id + "/artikelgruppe";
		    		Log.v(LOG_TAG, "path = " + path);
		    		final HttpResponse<Artikelgruppe> result = mock
		    				                                   ? Mock.sucheArtikelgruppeNachArtikel(id)
		    				                                   : WebServiceClient.getJsonSingle(path, Artikelgruppe.class);

					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikelgruppe> unused) {
					progressDialog.dismiss();
	    		}
			};

    		sucheArtikelgruppeNachArtikelTask.execute(id);
    		HttpResponse<Artikelgruppe> result = null;
	    	try {
	    		result = sucheArtikelgruppeNachArtikelTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
	    	
    		if (result.responseCode != HTTP_OK) {
	    		return result;
		    }
    		
    		setArtikelUri(result.resultObject);
		    return result;
		}
		
		private void setArtikelUri(Artikelgruppe artikelgruppe) {
	    	// URL der Artikel fuer Emulator anpassen
	    	final String artikelUri = artikelgruppe.artikelUri;
	    	if (!TextUtils.isEmpty(artikelUri)) {
			    artikelgruppe.artikelUri = artikelUri.replace(LOCALHOST, LOCALHOST_EMULATOR);
	    	}
		}
		
		public HttpResponse<Artikel> sucheArtikelNachArtikelgruppe(Long id, final Context ctx) {
			
			final AsyncTask<Long, Void, HttpResponse<Artikel>> sucheArtikelNachArtikelgruppeTask = new AsyncTask<Long, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(Long... ids) {
					final Long id = ids[0];
		    		final String path = ARTIKEL_PATH + "/artikelgruppe/" + id + "/artikel";
		    		Log.v(LOG_TAG, "path = " + path);
		    		final HttpResponse<Artikel> result = mock
		    				                                   ? Mock.sucheArtikelNachArtikelgruppe(id)
		    				                                   : WebServiceClient.getJsonList(path, Artikel.class);

					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};

    		sucheArtikelNachArtikelgruppeTask.execute(id);
    		HttpResponse<Artikel> result = null;
	    	try {
	    		result = sucheArtikelNachArtikelgruppeTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
	    	
    		if (result.responseCode != HTTP_OK) {
	    		return result;
		    }
    		
    		final ArrayList<Artikel> artikel = result.resultList;
	    	// URLs fuer Emulator anpassen
	    	for (Artikel a : artikel) {
	    		setArtikelgruppeUri(a);
	    	}
			return result;
		}
		
		public HttpResponse<Void> deleteArtikel(Long id, final Context ctx) {
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Artikel"
			final AsyncTask<Long, Void, HttpResponse<Void>> deleteArtikelTask = new AsyncTask<Long, Void, HttpResponse<Void>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Void> doInBackground(Long... ids) {
					final Long artikelId = ids[0];
		    		final String path = ARTIKEL_PATH + "/" + artikelId;
		    		Log.v(LOG_TAG, "path = " + path);

		    		final HttpResponse<Void> result = mock ? Mock.deleteArtikel(artikelId) : WebServiceClient.delete(path);
			    	return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Void> unused) {
					progressDialog.dismiss();
	    		}
			};
			
			deleteArtikelTask.execute(id);
			final HttpResponse<Void> result;
	    	try {
	    		result = deleteArtikelTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
			
			return result;
		}
		
		public HttpResponse<Artikel> updateArtikel(Artikel artikel, final Context ctx) {
			// (evtl. mehrere) Parameter vom Typ "Artikel", Resultat vom Typ "void"
			final AsyncTask<Artikel, Void, HttpResponse<Artikel>> updateArtikelTask = new AsyncTask<Artikel, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(Artikel... artikels) {
					final Artikel artikel = artikels[0];
		    		final String path = ARTIKEL_PATH;
		    		Log.v(LOG_TAG, "path = " + path);

		    		final HttpResponse<Artikel> result = mock
		    				                          ? Mock.updateArtikel(artikel)
		    		                                  : WebServiceClient.putJson(artikel, path);
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};
			
			updateArtikelTask.execute(artikel);
			final HttpResponse<Artikel> result;
			try {
				result = updateArtikelTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
			
			if (result.responseCode == HTTP_NO_CONTENT || result.responseCode == HTTP_OK) {
				artikel.updateVersion();  // kein konkurrierendes Update auf Serverseite
				result.resultObject = artikel;
			}
			
			return result;
	    }
		
		public HttpResponse<Artikel> createArtikel(Artikel artikel, final Context ctx) {
			// (evtl. mehrere) Parameter vom Typ "Artikel", Resultat vom Typ "void"
			final AsyncTask<Artikel, Void, HttpResponse<Artikel>> createArtikelTask = new AsyncTask<Artikel, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(Artikel... artikels) {
					final Artikel artikel = artikels[0];
		    		final String path = ARTIKEL_PATH;
		    		Log.v(LOG_TAG, "path = " + path);

		    		final HttpResponse<Artikel> result = mock
                                                               ? Mock.createArtikel(artikel)
                                                               : WebServiceClient.postJson(artikel, path);
		    		
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};
			
			createArtikelTask.execute(artikel);
			HttpResponse<Artikel> response = null; 
			try {
				response = createArtikelTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
			
			artikel.id = Long.valueOf(response.content);
			final HttpResponse<Artikel> result = new HttpResponse<Artikel>(response.responseCode, response.content, artikel);
			return result;
	    }
	}
}
