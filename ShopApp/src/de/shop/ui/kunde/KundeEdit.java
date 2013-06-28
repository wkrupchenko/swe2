package de.shop.ui.kunde;

import static de.shop.util.Konstanten.KUNDE_KEY;
import static de.shop.util.Konstanten.MAX_KATEGORIE;
import static de.shop.util.Konstanten.MIN_KATEGORIE;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Locale;

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
import de.shop.data.kunde.Kunde;
import de.shop.data.kunde.FamilienstandTyp;
import de.shop.data.kunde.GeschlechtTyp;
import de.shop.service.HttpResponse;
import de.shop.service.KundeService.KundeServiceBinder;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

public class KundeEdit extends Fragment {
	private static final String LOG_TAG = KundeEdit.class.getSimpleName();
	
	private Kunde kunde;
	private EditText edtNachname;
	private EditText edtVorname;
	private EditText edtEmail;
	private EditText edtPlz;
	private EditText edtOrt;
	private EditText edtStrasse;
	private EditText edtHausnr;
	private EditText edtRabatt;
	private EditText edtUmsatz;
	private DatePicker dpSeit;
	private RadioButton rbPrivatkunde;
	private RadioButton rbFirmenkunde;
	private ToggleButton tglNewsletter;
	private RadioButton rbMaennlich;
	private RadioButton rbWeiblich;
	private Spinner spFamilienstand;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		kunde = (Kunde) getArguments().get(KUNDE_KEY);
		Log.d(LOG_TAG, kunde.toString());
        
		// Voraussetzung fuer onOptionsItemSelected()
		setHasOptionsMenu(true);
		
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.kunde_edit, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
    	final TextView txtId = (TextView) view.findViewById(R.id.kunde_id);
    	txtId.setText(String.valueOf(kunde.id));

    	edtNachname = (EditText) view.findViewById(R.id.nachname_edt);
    	edtNachname.setText(kunde.nachname);
    	
    	edtVorname = (EditText) view.findViewById(R.id.vorname_edt);
    	edtVorname.setText(kunde.vorname);
    	
    	edtEmail = (EditText) view.findViewById(R.id.email_edt);
    	edtEmail.setText(kunde.email);
    	
    	edtPlz = (EditText) view.findViewById(R.id.plz_edt);
    	edtPlz.setText(kunde.adresse.plz);
    	
    	edtOrt = (EditText) view.findViewById(R.id.ort_edt);
    	edtOrt.setText(kunde.adresse.ort);
    	
    	edtStrasse = (EditText) view.findViewById(R.id.strasse_edt);
    	edtStrasse.setText(kunde.adresse.strasse);
    	
    	edtHausnr = (EditText) view.findViewById(R.id.hausnr_edt);
    	edtHausnr.setText(kunde.adresse.hausnr);

    	edtRabatt = (EditText) view.findViewById(R.id.rabatt_edt);
    	edtHausnr.setText(String.valueOf(kunde.rabatt));

    	edtHausnr = (EditText) view.findViewById(R.id.umsatz_edt);
    	edtHausnr.setText(String.valueOf(kunde.umsatz));
    	
    	dpSeit = (DatePicker) view.findViewById(R.id.seit);
    	final GregorianCalendar cal = new GregorianCalendar(Locale.getDefault());
    	cal.setTime(kunde.seit);
    	final int jahr = cal.get(YEAR);
    	final int monat = cal.get(MONTH);
    	final int tag = cal.get(DAY_OF_MONTH);
    	dpSeit.init(jahr, monat, tag, null);

    	rbPrivatkunde = (RadioButton) view.findViewById(R.id.privatkunde);
    	rbFirmenkunde = (RadioButton) view.findViewById(R.id.firmenkunde);
    	
    	tglNewsletter = (ToggleButton) view.findViewById(R.id.newsletter_tgl);
    	tglNewsletter.setChecked(kunde.newsletter);
    	
    	rbMaennlich = (RadioButton) view.findViewById(R.id.maennlich);
    	rbWeiblich = (RadioButton) view.findViewById(R.id.weiblich);
    	
    	spFamilienstand = (Spinner) view.findViewById(R.id.familienstand);
    	
    	if (kunde.geschlecht != null) {
	    	switch (kunde.geschlecht) {
		    	case MAENNLICH:
		        	rbMaennlich.setChecked(true);
			    	break;
			    	
		    	case WEIBLICH:
		        	rbWeiblich.setChecked(true);
			    	break;
			    	
			    default:
	    	}
    	}
	    	
		switch (kunde.familienstand) {
	    	case LEDIG:
	    		spFamilienstand.setSelection(0);
		    	break;
	    	case VERHEIRATET:
	    		spFamilienstand.setSelection(1);
		    	break;
	    	case GESCHIEDEN:
	    		spFamilienstand.setSelection(2);
		    	break;
	    	case VERWITWET:
	    		spFamilienstand.setSelection(3);
		    	break;
		    	
		    default:
		}
    }
    
	@Override
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.kunde_edit_options, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.speichern:
				setKunde();

				final Activity activity = getActivity();
				
				// Das Fragment KundeEdit kann von Main und von KundeListe aus aufgerufen werden
				KundeServiceBinder kundeServiceBinder;
				if (Main.class.equals(activity.getClass())) {
					Main main = (Main) activity;
					kundeServiceBinder = main.getKundeServiceBinder();
				}
				else if (KundenListe.class.equals(activity.getClass())) {
					KundenListe kundenListe = (KundenListe) activity;
					kundeServiceBinder = kundenListe.getKundeServiceBinder();
				}
				else {
					return true;
				}
				
				final HttpResponse<Kunde> result = kundeServiceBinder.updateKunde((Kunde) kunde, activity);
				final int statuscode = result.responseCode;
				if (statuscode != HTTP_NO_CONTENT && statuscode != HTTP_OK) {
					String msg = null;
					switch (statuscode) {
						case HTTP_CONFLICT:
							msg = result.content;
							break;
						case HTTP_UNAUTHORIZED:
							msg = getString(R.string.s_error_prefs_login, kunde.id);
							break;
						case HTTP_FORBIDDEN:
							msg = getString(R.string.s_error_forbidden, kunde.id);
							break;
					}
					
		    		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		    		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    };
		    		builder.setMessage(msg)
		    		       .setNeutralButton(getString(R.string.s_ok), listener)
		    		       .create()
		    		       .show();
		    		return true;
				}
				
				kunde = result.resultObject;  // ggf. erhoehte Versionsnr. bzgl. konkurrierender Updates
				
				// Gibt es in der Navigationsleiste eine KundenListe? Wenn ja: Refresh mit geaendertem Kunde-Objekt
				final Fragment fragment = getFragmentManager().findFragmentById(R.id.kunden_liste_nav);
				if (fragment != null) {
					final KundenListeNav kundenListeFragment = (KundenListeNav) fragment;
					kundenListeFragment.refresh(kunde);
				}
				
				final Bundle args = new Bundle(1);
				args.putSerializable(KUNDE_KEY, kunde);
				
				final Fragment neuesFragment = new KundeDetails();
				neuesFragment.setArguments(args);
				
				// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
				getFragmentManager().beginTransaction()
				                    .replace(R.id.details, neuesFragment)
				                    .addToBackStack(null)  
				                    .commit();
				return true;
				
			case R.id.einstellungen:
				getFragmentManager().beginTransaction()
                                    .replace(R.id.details, new Prefs())
                                    .addToBackStack(null)
                                    .commit();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void setKunde() {
		kunde.nachname = edtNachname.getText().toString();
		
		kunde.vorname = edtVorname.getText().toString();
		kunde.email = edtEmail.getText().toString();
		kunde.adresse.plz = edtPlz.getText().toString();
		kunde.adresse.ort = edtOrt.getText().toString();
		kunde.adresse.strasse = edtStrasse.getText().toString();
		kunde.adresse.hausnr = edtHausnr.getText().toString();
		kunde.rabatt = Double.valueOf(edtRabatt.getText().toString());
		kunde.umsatz = Double.valueOf(edtUmsatz.getText().toString());
		
		final GregorianCalendar cal = new GregorianCalendar(Locale.getDefault());
		cal.set(dpSeit.getYear(), dpSeit.getMonth(), dpSeit.getDayOfMonth());
		kunde.seit = cal.getTime();

		if (rbPrivatkunde.isChecked()) {
			kunde.art = "P";
		} else if (rbFirmenkunde.isChecked()) {
			kunde.art = "F";
		}
		else {
			kunde.art = "P";
		}
		
		kunde.newsletter = tglNewsletter.isChecked();
		
		if (rbMaennlich.isChecked()) {
			kunde.geschlecht = GeschlechtTyp.MAENNLICH;
		} else if (rbWeiblich.isChecked()) {
			kunde.geschlecht = GeschlechtTyp.WEIBLICH;
		}
		else {
			kunde.geschlecht = GeschlechtTyp.MAENNLICH;
		}
		
		final int familienstandPos = spFamilienstand.getSelectedItemPosition();
		kunde.familienstand = FamilienstandTyp.valueOf(familienstandPos);
		
		Log.d(LOG_TAG, kunde.toString());
	}
}
