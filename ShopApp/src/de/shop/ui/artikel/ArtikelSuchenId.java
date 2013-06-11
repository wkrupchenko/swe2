package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;
import de.shop.R;
import de.shop.data.artikel.Artikel;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ArtikelSuchenId extends Activity implements OnClickListener {
	private static final String LOG_TAG = ArtikelSuchenId.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suche_artikel_id);
		findViewById(R.id.btn_suchen).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		EditText artikelIdTxt = (EditText) findViewById(R.id.artikel_id);
		String artikelId = artikelIdTxt.getText().toString();

		
		Artikel artikel = getArtikel(artikelId);
				
		final Intent intent = new Intent(view.getContext(), ArtikelDetails.class);
		intent.putExtra(ARTIKEL_KEY, artikel);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private Artikel getArtikel(String artikelIdStr) {
    	final Long artikelId = Long.valueOf(artikelIdStr);
    	final Artikel artikel = new Artikel(artikelId, "Bezeichnung" + artikelIdStr);
    	Log.v(LOG_TAG, artikel.toString());
    	
    	return artikel;
    }
}