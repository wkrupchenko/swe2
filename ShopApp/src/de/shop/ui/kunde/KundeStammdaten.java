package de.shop.ui.kunde;

import static de.shop.util.Konstanten.KUNDE_KEY;
import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.shop.R;
import de.shop.data.kunde.Kunde;
import de.shop.ui.main.Prefs;
import de.shop.util.WischenListener;

public class KundeStammdaten extends Fragment implements OnTouchListener {
	private static final String LOG_TAG = KundeStammdaten.class.getSimpleName();
	
	private Kunde kunde;
	private GestureDetector gestureDetector;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        kunde = (Kunde) getArguments().get(KUNDE_KEY);
        Log.d(LOG_TAG, kunde.toString());

        // Voraussetzung fuer onOptionsItemSelected()
        setHasOptionsMenu(true);
        
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.kunde_stammdaten, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		fillValues(view);
    	
    	final Activity activity = getActivity();
	    final OnGestureListener onGestureListener = new WischenListener(activity);
	    gestureDetector = new GestureDetector(activity, onGestureListener);  // Context und OnGestureListener als Argumente
	    view.setOnTouchListener(this);
    }
	
	private void fillValues(View view) {
		final TextView txtId = (TextView) view.findViewById(R.id.kunde_id);
    	txtId.setText(kunde.id.toString());
    	
    	final TextView txtArt = (TextView) view.findViewById(R.id.art);
    	txtArt.setText(kunde.art);
    	
    	final TextView txtNachname = (TextView) view.findViewById(R.id.nachname);
    	txtNachname.setText(kunde.nachname);
    	
    	final TextView txtVorname = (TextView) view.findViewById(R.id.vorname);
    	txtVorname.setText(kunde.vorname);
    	
    	final TextView txtEmail = (TextView) view.findViewById(R.id.email);
    	txtEmail.setText(kunde.email);
    	
    	final TextView txtUmsatz = (TextView) view.findViewById(R.id.umsatz);
    	txtUmsatz.setText(String.valueOf(kunde.umsatz));
    	
    	final TextView txtRabatt = (TextView) view.findViewById(R.id.rabatt);
    	txtRabatt.setText(String.valueOf(kunde.rabatt));
    	
    	final TextView txtSeit = (TextView) view.findViewById(R.id.seit);
		final String seitStr = DateFormat.getDateFormat(view.getContext()).format(kunde.seit);
    	txtSeit.setText(seitStr);
    	
    	 
    	final TextView txtPlz = (TextView) view.findViewById(R.id.plz);
    	txtPlz.setText(kunde.adresse.plz);
    	
    	final TextView txtOrt = (TextView) view.findViewById(R.id.ort);
    	txtOrt.setText(kunde.adresse.ort);
    	
    	final TextView txtStrasse = (TextView) view.findViewById(R.id.strasse);
    	txtStrasse.setText(kunde.adresse.strasse);
    	
    	if (kunde.adresse.hausnr != null && !kunde.adresse.hausnr.isEmpty()) {
	    	final TextView txtHausnr = (TextView) view.findViewById(R.id.hausnr);
	    	txtHausnr.setText(kunde.adresse.hausnr);
    	}
    	 
    	
    	final RadioButton rbMaennlich = (RadioButton) view.findViewById(R.id.maennlich);
    	final RadioButton rbWeiblich = (RadioButton) view.findViewById(R.id.weiblich);
    	final Spinner spFamilienstand = (Spinner) view.findViewById(R.id.familienstand); 
    	
    	 
    		final Kunde kunde =  new Kunde();
    		
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
	    	
	    	if (kunde.familienstand != null) {
	    		spFamilienstand.setSelection(kunde.familienstand.value());
	    	}   	 
    	 
    		rbMaennlich.setEnabled(true);
    		rbWeiblich.setEnabled(true);
    		spFamilienstand.setEnabled(true);
    		    	 
	}

	@Override
	// http://developer.android.com/guide/topics/ui/actionbar.html#ChoosingActionItems :
	// "As a general rule, all items in the options menu (let alone action items) should have a global impact on the app,
	//  rather than affect only a small portion of the interface."
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.kunde_stammdaten_options, menu);
		
		// "Searchable Configuration" in res\xml\searchable.xml wird der SearchView zugeordnet
		final Activity activity = getActivity();
	    final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
	    final SearchView searchView = (SearchView) menu.findItem(R.id.suchen).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
	}

	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit:
				// Evtl. vorhandene Tabs der ACTIVITY loeschen
		    	getActivity().getActionBar().removeAllTabs();
		    	
				final Bundle args = new Bundle(1);
				args.putSerializable(KUNDE_KEY, kunde);
				
				final Fragment neuesFragment = new KundeEdit();
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
	*/
	
	@Override
	// OnTouchListener
	public boolean onTouch(View view, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
}