package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import de.shop.R;
import de.shop.service.HttpResponse;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;
import de.shop.data.artikel.Artikel;

public class ArtikelSuchenBezeichnung extends Fragment implements OnClickListener {
	private EditText artikelBezeichnungTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.suche_artikel_bezeichnung, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		artikelBezeichnungTxt = (EditText) view.findViewById(R.id.artikel_bezeichnung_edt);
    	
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
				suchen(view);
				break;
				
			default:
				break;
		}
    }
	
	private void suchen(View view) {
		final Context ctx = view.getContext();

		final String artikelBezeichnung = artikelBezeichnungTxt.getText().toString();
		if (TextUtils.isEmpty(artikelBezeichnung)) {
			artikelBezeichnungTxt.setError(getString(R.string.a_bezeichnung_fehlt));
    		return;
    	}
		
		final Main mainActivity = (Main) getActivity();
		final HttpResponse<? extends Artikel> result = mainActivity.getArtikelServiceBinder().sucheArtikelNachBezeichnung(artikelBezeichnung, ctx);

		if (result.responseCode == HTTP_NOT_FOUND) {
			final String msg = getString(R.string.a_artikel_not_found, artikelBezeichnung);
			artikelBezeichnungTxt.setError(msg);
			return;
		}
		
		final Intent intent = new Intent(mainActivity, ArtikelListe.class);
		intent.putExtra(ARTIKEL_KEY, result.resultList);
		startActivity(intent);
	}
}
