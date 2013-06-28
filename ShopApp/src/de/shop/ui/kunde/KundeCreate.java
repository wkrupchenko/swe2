package de.shop.ui.kunde;

import static de.shop.util.Konstanten.KUNDE_KEY;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static de.shop.ui.main.Prefs.protocol;
import static de.shop.ui.main.Prefs.host;
import static de.shop.ui.main.Prefs.port;
import static de.shop.ui.main.Prefs.path;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.shop.R;
import de.shop.data.kunde.Adresse;
import de.shop.data.kunde.FamilienstandTyp;
import de.shop.data.kunde.GeschlechtTyp;
import de.shop.data.kunde.Kunde;
import de.shop.service.HttpResponse;
import de.shop.service.ArtikelService.ArtikelServiceBinder;
import de.shop.service.KundeService.KundeServiceBinder;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

public class KundeCreate {
	private static final String LOG_TAG = KundeCreate.class.getSimpleName();
//	
//	private Kunde kunde;
//	private EditText edtNachname;
//	private EditText edtVorname;
//	private EditText edtEmail;
//	private EditText edtFamilienstand;
//	private EditText edtArt;	 
//	public GeschlechtTyp geschlecht;
//	public FamilienstandTyp familienstand;
//	private EditText rabatt;
//	private EditText umsatz;
//	public Date seit;
//	public boolean newsletter;
//	public Adresse adresse;
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		kunde = new Kunde();
//		Log.d(LOG_TAG, kunde.toString());
//        
//		// Voraussetzung fuer onOptionsItemSelected()
//		setHasOptionsMenu(true);
//		
//		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
//		return inflater.inflate(R.layout.artikel_create, container, false);
//	}
//	
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//    	final TextView txtId = (TextView) view.findViewById(R.id.artikel_crt);
//    	txtId.setText(String.valueOf(kunde.id));
//
//    	edtBezeichnung = (EditText) view.findViewById(R.id.bezeichnung_crt);
//    	edtBezeichnung.setText(artikel.bezeichnung);
//    	
//    	edtPreis = (EditText) view.findViewById(R.id.preis_crt);
//    	edtPreis.setText(String.valueOf(artikel.preis));
//    	
//    	tglErhaeltlich = (ToggleButton) view.findViewById(R.id.erhaeltlich_crt);
//    	tglErhaeltlich.setChecked(artikel.erhaeltlich);
//    	
//    	edtArtikelgruppe = (EditText) view.findViewById(R.id.artikelgruppe_crt);
//    	edtArtikelgruppe.setText(String.valueOf(artikel.artikelgruppeUri));
//    	
//    }
//    
//	@Override
//	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		super.onCreateOptionsMenu(menu, inflater);
//		inflater.inflate(R.menu.kunde_create_options, menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case R.id.speichern:
//				setKunde();
//
//				Main main = (Main) getActivity();
//				KundeServiceBinder kundeServiceBinder = main.getKundeServiceBinder();
//				
//				final HttpResponse<Kunde> result = kundeServiceBinder.createKunde(kunde, main);
//				final int statuscode = result.responseCode;
//				if (statuscode != HTTP_NO_CONTENT && statuscode != HTTP_CREATED) {
//					String msg = null;
//					switch (statuscode) {
//						case HTTP_CONFLICT:
//							msg = result.content;
//							break;
//						case HTTP_UNAUTHORIZED:
//							msg = getString(R.string.s_error_prefs_login, kunde.id);
//							break;
//						case HTTP_FORBIDDEN:
//							msg = getString(R.string.s_error_forbidden, kunde.id);
//							break;
//					}
//					
//		    		final AlertDialog.Builder builder = new AlertDialog.Builder(main);
//		    		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {}
//                    };
//		    		builder.setMessage(msg)
//		    		       .setNeutralButton(getString(R.string.s_ok), listener)
//		    		       .create()
//		    		       .show();
//		    		return true;
//				}
//				
//				kunde = result.resultObject;  // ggf. erhoehte Versionsnr. bzgl. konkurrierender Updates
//				
//				
//				final Bundle args = new Bundle(1);
//				args.putSerializable(KUNDE_KEY, artikel);
//				
//				final Fragment neuesFragment = new KundeDetails();
//				neuesFragment.setArguments(args);
//				
//				// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
//				getFragmentManager().beginTransaction()
//				                    .replace(R.id.details, neuesFragment)
//				                    .addToBackStack(null)  
//				                    .commit();
//				return true;
//				
//			case R.id.menu_settings:
//				getFragmentManager().beginTransaction()
//                                    .replace(R.id.details, new Prefs())
//                                    .addToBackStack(null)
//                                    .commit();
//				return true;
//				
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//	}
//	
//	private void setKunde() {
//		
//		kunde.id = Long.valueOf(0);
//
//		kunde.bezeichnung = edtBezeichnung.getText().toString();
//		
//		kunde.preis = Double.valueOf(edtPreis.getText().toString());
//		
//		kunde.erhaeltlich = tglErhaeltlich.isChecked();
//		
//		kunde.artikelgruppeUri = protocol + "://" + host + ":" + port + path + "/artikel/artikelgruppe/" + edtArtikelgruppe.getText().toString();
//
//		Log.d(LOG_TAG, kunde.toString());
//	}
}
 
