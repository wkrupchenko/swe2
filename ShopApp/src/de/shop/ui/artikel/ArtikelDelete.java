package de.shop.ui.artikel;

import static android.widget.Toast.LENGTH_LONG;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;
import de.shop.R;
import de.shop.service.HttpResponse;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;
import de.shop.ui.main.Startseite;

public class ArtikelDelete extends Fragment implements OnClickListener {
	
	private EditText artikelIdTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_delete, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		artikelIdTxt = (EditText) view.findViewById(R.id.artikel_id);
    	
		// KundeSucheId (this) ist gleichzeitig der Listener, wenn der Delete-Button angeklickt wird
		// und implementiert deshalb die Methode onClick() unten
    	view.findViewById(R.id.btn_delete).setOnClickListener(this);
    	
	    // Evtl. vorhandene Tabs der ACTIVITY loeschen
    	final ActionBar actionBar = getActivity().getActionBar();
    	actionBar.setDisplayShowTitleEnabled(true);
    	actionBar.removeAllTabs();
    }
	
	@Override
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
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
	// OnClickListener
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_delete:
				final String artikelIdStr = artikelIdTxt.getText().toString();
				if (TextUtils.isEmpty(artikelIdStr)) {
		    		Toast.makeText(view.getContext(), R.string.a_artikelnr_fehlt, LENGTH_LONG).show();
		    		return;
		    	}
				
				final Context ctx = view.getContext();
				final Long artikelId = Long.valueOf(artikelIdStr);
				final Main mainActivity = (Main) getActivity();
				final HttpResponse<Void> result = mainActivity.getArtikelServiceBinder().deleteArtikel(artikelId, ctx);
				final int statuscode = result.responseCode;
				if (statuscode != HTTP_NO_CONTENT && statuscode != HTTP_OK) {
					String msg = null;
					switch (statuscode) {
						case HTTP_CONFLICT:
							msg = result.content;
							break;
						case HTTP_UNAUTHORIZED:
							msg = getString(R.string.s_error_prefs_login, artikelId);
							break;
						case HTTP_FORBIDDEN:
							msg = getString(R.string.s_error_forbidden, artikelId);
							break;
					}
					
		    		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    };
		    		builder.setMessage(msg)
		    		       .setNeutralButton(getString(R.string.s_ok), listener)
		    		       .create()
		    		       .show();
		    		return;
				}
				
				final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                };
                String msg = getString(R.string.s_artikel_delete_ok, artikelId);
	    		builder.setMessage(msg)
	    		       .setNeutralButton(getString(R.string.s_ok), listener)
	    		       .create()
	    		       .show();
				
				// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
				getFragmentManager().beginTransaction()
				                    .replace(R.id.details, new Startseite())
				                    .addToBackStack(null)
				                    .commit();
				break;
				
			default:
				break;
		}
    }
}
