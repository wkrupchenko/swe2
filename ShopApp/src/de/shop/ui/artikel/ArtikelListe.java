package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import de.shop.R;
import de.shop.data.artikel.Artikel;
import de.shop.service.ArtikelService;
import de.shop.service.ArtikelService.ArtikelServiceBinder;

public class ArtikelListe extends Activity {
	private ArtikelServiceBinder artikelServiceBinder;
	
	// ServiceConnection ist ein Interface: anonyme Klasse verwenden, um ein Objekt davon zu erzeugen
	private ServiceConnection artikelServiceConnection = new ServiceConnection() {
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
        setContentView(R.layout.artikel_liste);
        
        final Fragment details = new ArtikelDetails();
		final Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	@SuppressWarnings("unchecked")
			final List<Artikel> artikel = (List<Artikel>) extras.get(ARTIKEL_KEY);
        	if (artikel != null && !artikel.isEmpty()) {
        		final Bundle args = new Bundle(1);
        		args.putSerializable(ARTIKEL_KEY, artikel.get(0));
        		details.setArguments(args);
        	}
        }
		
        getFragmentManager().beginTransaction()
                            .add(R.id.details, details)
                            .commit();
    }
	
    @Override
	public void onStart() {
		super.onStart();
		
		Intent intent = new Intent(this, ArtikelService.class);
		bindService(intent, artikelServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
	@Override
	public void onStop() {
		super.onStop();
		
		unbindService(artikelServiceConnection);
	}

	public ArtikelServiceBinder getKundeServiceBinder() {
		return artikelServiceBinder;
	}
}
