package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import de.shop.R;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;
import de.shop.data.artikel.Artikel;

public class ArtikelSuchenId extends Fragment implements OnClickListener {
	private EditText artikelIdTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.suche_artikel_id, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		artikelIdTxt = (EditText) view.findViewById(R.id.artikel_id_edt);
    	
		view.findViewById(R.id.btn_suchen).setOnClickListener(this);
		
		final ActionBar actionBar = getActivity().getActionBar();
    	actionBar.setDisplayShowTitleEnabled(true);
    	actionBar.removeAllTabs();
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
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
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_suchen:
				final String artikelIdStr = artikelIdTxt.getText().toString();
				final Long artikelId = Long.valueOf(artikelIdStr);
				final Main mainActivity = (Main) getActivity();
				final Artikel artikel = mainActivity.getArtikelServiceBinder().sucheArtikelNachId(artikelId);
				
				final Bundle args = new Bundle(1);
				args.putSerializable(ARTIKEL_KEY, artikel);
				
				final Fragment neuesFragment = new ArtikelDetails();
				neuesFragment.setArguments(args);
				
				// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
				getFragmentManager().beginTransaction()
				                    .replace(R.id.details, neuesFragment)
				                    .addToBackStack(null)
				                    .commit();
				break;
				
			default:
				break;
		}
    }
}
