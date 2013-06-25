package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import de.shop.data.artikel.Artikel;
import de.shop.service.ArtikelService;
import de.shop.service.ArtikelService.ArtikelServiceBinder;

public class SucheArtikelgruppeActivity extends Activity {
	private static final String LOG_TAG = SucheArtikelgruppeActivity.class.getSimpleName();
	
	private ArtikelServiceBinder artikelServiceBinder;
	
	// ServiceConnection ist ein Interface: anonyme Klasse verwenden, um ein Objekt davon zu erzeugen
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			artikelServiceBinder = (ArtikelServiceBinder) serviceBinder;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			artikelServiceBinder = null;
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.search);

		final Intent intent = getIntent();
		if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
			return;
		}
		
		final Long query = Long.valueOf(intent.getStringExtra(SearchManager.QUERY));
		Log.d(LOG_TAG, query.toString());
		
		suchen(query);
	}
	
	@Override
	public void onResume() {
		final Intent intent = new Intent(this, ArtikelService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		super.onResume();
	}

	@Override
	public void onPause() {
		unbindService(serviceConnection);
		super.onPause();
	}
	
	private void suchen(Long id) {
		final ArrayList<? extends Artikel> artikel = artikelServiceBinder.sucheArtikelNachArtikelgruppe(id, this).resultList;
		Log.d(LOG_TAG, id.toString());
		
		final Intent intent = new Intent(this, ArtikelListe.class);
		if (artikel != null && !artikel.isEmpty()) {
			intent.putExtra(ARTIKEL_KEY, artikel); 
		}
		startActivity(intent);
	}
}
