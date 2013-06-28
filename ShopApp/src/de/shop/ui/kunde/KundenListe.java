package de.shop.ui.kunde;

import static de.shop.util.Konstanten.KUNDE_KEY;
import static de.shop.util.Konstanten.KUNDEN_KEY;

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
import de.shop.data.kunde.Kunde;
import de.shop.service.BestellungService;
import de.shop.service.KundeService;
import de.shop.service.BestellungService.BestellungServiceBinder;
import de.shop.service.KundeService.KundeServiceBinder;

public class KundenListe extends Activity {
	private KundeServiceBinder kundeServiceBinder;
	private BestellungServiceBinder bestellungServiceBinder;
	
	// ServiceConnection ist ein Interface: anonyme Klasse verwenden, um ein Objekt davon zu erzeugen
	private ServiceConnection kundeServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			kundeServiceBinder = (KundeServiceBinder) serviceBinder;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			kundeServiceBinder = null;
		}
	};
	
	private ServiceConnection bestellungServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			bestellungServiceBinder = (BestellungServiceBinder) serviceBinder;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bestellungServiceBinder = null;
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kunden_liste);
        
        final Fragment details = new KundeDetails();
		final Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	@SuppressWarnings("unchecked")
			final List<Kunde> kunden = (List<Kunde>) extras.get(KUNDEN_KEY);
        	if (kunden != null && !kunden.isEmpty()) {
        		final Bundle args = new Bundle(1);
        		args.putSerializable(KUNDE_KEY, kunden.get(0));
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
		
		Intent intent = new Intent(this, KundeService.class);
		bindService(intent, kundeServiceConnection, Context.BIND_AUTO_CREATE);
		
		intent = new Intent(this, BestellungService.class);
		bindService(intent, bestellungServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
	@Override
	public void onStop() {
		super.onStop();
		
		unbindService(kundeServiceConnection);
		unbindService(bestellungServiceConnection);
	}

	public KundeServiceBinder getKundeServiceBinder() {
		return kundeServiceBinder;
	}

	public BestellungServiceBinder getBestellungServiceBinder() {
		return bestellungServiceBinder;
	}
}
