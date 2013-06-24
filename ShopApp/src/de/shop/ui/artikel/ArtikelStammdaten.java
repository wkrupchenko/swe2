package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.SearchView;
import android.widget.TextView;

import de.shop.R;
import de.shop.data.artikel.Artikel;
import de.shop.ui.main.Prefs;
import de.shop.util.WischenListener;

public class ArtikelStammdaten extends Fragment implements OnTouchListener {
	private static final String LOG_TAG = ArtikelStammdaten.class.getSimpleName();
	
	private Artikel artikel;
	private GestureDetector gestureDetector;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artikel = (Artikel) getArguments().get(ARTIKEL_KEY);
        Log.d(LOG_TAG, artikel.toString());

        // Voraussetzung fuer onOptionsItemSelected()
        setHasOptionsMenu(true);
        
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_stammdaten, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		final TextView txtId = (TextView) view.findViewById(R.id.artikel_id);
    	txtId.setText(artikel.id.toString());
    	
    	final TextView txtName = (TextView) view.findViewById(R.id.bezeichnung_txt);
    	txtName.setText(artikel.bezeichnung);
    	
    	final TextView txtErhaeltlich = (TextView) view.findViewById(R.id.erhaeltlich_txt);
    	txtErhaeltlich.setText(String.valueOf(artikel.erhaeltlich));
    	
    	final TextView txtPreis = (TextView) view.findViewById(R.id.preis_txt);
    	txtPreis.setText(String.valueOf(artikel.preis));
    	
    	final TextView txtArtikelgruppe = (TextView) view.findViewById(R.id.artikelgruppe_txt);
    	txtArtikelgruppe.setText(artikel.artikelgruppeUri);
    	
    	final Activity activity = getActivity();
	    final OnGestureListener onGestureListener = new WischenListener(activity);
	    gestureDetector = new GestureDetector(activity, onGestureListener);  // Context und OnGestureListener als Argumente
	    view.setOnTouchListener(this);
    }

	@Override
	// http://developer.android.com/guide/topics/ui/actionbar.html#ChoosingActionItems :
	// "As a general rule, all items in the options menu (let alone action items) should have a global impact on the app,
	//  rather than affect only a small portion of the interface."
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.artikel_stammdaten_options, menu);
		
		// "Searchable Configuration" in res\xml\searchable.xml wird der SearchView zugeordnet
		final Activity activity = getActivity();
	    final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
	    final SearchView searchView = (SearchView) menu.findItem(R.id.suchen).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				getFragmentManager().beginTransaction()
                                    .replace(R.id.details, new Prefs())
                                    .addToBackStack(null)
                                    .commit();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	// OnTouchListener
	public boolean onTouch(View view, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
}
