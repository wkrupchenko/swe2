package de.shop.ui.kunde;

import de.shop.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class KundeSuchenId extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suche_kunde_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}