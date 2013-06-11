package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import de.shop.R;
import de.shop.data.artikel.Artikel;

public class ArtikelDetails extends Activity {
	private static final String LOG_TAG = ArtikelSuchenId.class.getSimpleName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artikel_details);
        
        final Bundle extras = getIntent().getExtras();
        if (extras == null) {
        	return;
        }

        final Artikel artikel = (Artikel) extras.getSerializable(ARTIKEL_KEY);
        Log.d(LOG_TAG, artikel.toString());
        fillValues(artikel);
    }
    
    private void fillValues(Artikel artikel) {
        final TextView txtId = (TextView) findViewById(R.id.artikel_id);
    	txtId.setText(artikel.id.toString());
    	
    	final TextView txtBezeichnung = (TextView) findViewById(R.id.bezeichnung);
    	txtBezeichnung.setText(artikel.bezeichnung);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
