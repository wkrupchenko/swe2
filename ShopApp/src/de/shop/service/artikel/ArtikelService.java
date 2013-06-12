package de.shop.service.artikel;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import de.shop.data.artikel.Artikel;

public class ArtikelService extends Service {
	public static final String LOG_TAG = ArtikelService.class.getSimpleName();
	
	private final ArtikelServiceBinder binder = new ArtikelServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class ArtikelServiceBinder extends Binder {
		
		// Aufruf in einem eigenen Thread
		public Artikel getArtikel(Long id) {
			
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Artikel"
			final AsyncTask<Long, Void, Artikel> getArtikelTask = new AsyncTask<Long, Void, Artikel>() {

				@Override
	    		protected void onPreExecute() {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread starten ...");
				}
				
				@Override
				// Neuer Thread (hier: Emulation des REST-Aufrufs), damit der UI-Thread nicht blockiert wird
				protected Artikel doInBackground(Long... ids) {
					final Long artikelId = ids[0];
					final Artikel artikel = new Artikel(artikelId, "Bezeichnung" + artikelId);
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + artikel);
					return artikel;
				}
				
				@Override
	    		protected void onPostExecute(Artikel artikel) {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread beenden ...");
	    		}
			};
			
	    	getArtikelTask.execute(id);
	    	Artikel artikel = null;
	    	try {
				artikel = getArtikelTask.get(3L, TimeUnit.SECONDS);
			}
	    	catch (Exception e) {
	    		Log.e(LOG_TAG, e.getMessage(), e);
			}
	    	
	    	return artikel;
		}
	}
}
