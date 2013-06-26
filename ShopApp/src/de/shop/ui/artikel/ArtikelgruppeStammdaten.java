package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import de.shop.R;
import de.shop.data.artikel.Artikel;
import de.shop.data.artikel.Artikelgruppe;
import de.shop.service.HttpResponse;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;
import de.shop.util.WischenListener;

public class ArtikelgruppeStammdaten extends Fragment implements OnTouchListener {
	private static final String LOG_TAG = ArtikelStammdaten.class.getSimpleName();
	
	private Artikel artikel;
	private Artikelgruppe artikelgruppe;
	private GestureDetector gestureDetector;
	private List<Artikel> artikelList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artikel = (Artikel) getArguments().get(ARTIKEL_KEY);
        
        // Artikelgruppe suchen
        final Context ctx = inflater.getContext();
        Main mainActivity;
        ArtikelListe listeActivity;
        try {
        	mainActivity = (Main) getActivity();
        }
        catch(ClassCastException e) {
        	mainActivity = null;
        }
        HttpResponse<? extends Artikelgruppe> result;
        if(mainActivity != null) {
        	result = mainActivity.getArtikelServiceBinder().sucheArtikelgruppeNachArtikel(artikel.id, ctx);
        }
        else {
        	listeActivity = (ArtikelListe) getActivity();
        	result = listeActivity.getArtikelServiceBinder().sucheArtikelgruppeNachArtikel(artikel.id, ctx);
		}

		if (result.responseCode == HTTP_NOT_FOUND) {
			final String msg = getString(R.string.a_artikelgruppe_not_found, artikel.id);
		}
		
		artikelgruppe= result.resultObject;
        
        Log.d(LOG_TAG, artikelgruppe.toString());
        
        // Alle Artikel suchen
        final HttpResponse<? extends Artikel> result2;
        if(mainActivity != null) {
        	result2 = mainActivity.getArtikelServiceBinder().sucheArtikelNachArtikelgruppe(artikelgruppe.id, ctx);
        }
        else {
        	listeActivity = (ArtikelListe) getActivity();
        	result2 = listeActivity.getArtikelServiceBinder().sucheArtikelNachArtikelgruppe(artikelgruppe.id, ctx);
        }
        artikelList = (List<Artikel>) result2.resultList;
        

        // Voraussetzung fuer onOptionsItemSelected()
        setHasOptionsMenu(true);
        
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikelgruppe_stammdaten, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		final TextView txtId = (TextView) view.findViewById(R.id.artikelgruppe_id);
    	txtId.setText(artikelgruppe.id.toString());
    	
    	final TextView txtName = (TextView) view.findViewById(R.id.artikelgruppe_bezeichnung_txt);
    	txtName.setText(artikelgruppe.bezeichnung);
    	
    	final TableLayout lay = (TableLayout) view.findViewById(R.id.artikelgruppe_details_artikel);
    	lay.setStretchAllColumns(true);  
    	lay.setShrinkAllColumns(true);
    	int i = 1;
    	for(Artikel a : artikelList) {
    		final TableRow row  = new TableRow(view.getContext());
    		
    		final TextView txtIdA = new TextView(view.getContext());
    		txtIdA.setText(a.id.toString());
    		txtIdA.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    		row.addView(txtIdA, new TableRow.LayoutParams(0));
    		
    		final TextView txtNameA = new TextView(view.getContext());
    		txtNameA.setText(a.bezeichnung);
    		txtNameA.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    		row.addView(txtNameA, new TableRow.LayoutParams(1));
    		
    		final TextView txtPreisA = new TextView(view.getContext());
    		txtPreisA.setText(String.valueOf(a.preis));
    		txtPreisA.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    		row.addView(txtPreisA, new TableRow.LayoutParams(2));
    		
    		lay.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	}
    	
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
		inflater.inflate(R.menu.artikelgruppe_stammdaten_options, menu);
		
		// "Searchable Configuration" in res\xml\searchable.xml wird der SearchView zugeordnet
		/*final Activity activity = getActivity();
	    final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
	    final SearchView searchView = (SearchView) menu.findItem(R.id.suchen).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));*/
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
