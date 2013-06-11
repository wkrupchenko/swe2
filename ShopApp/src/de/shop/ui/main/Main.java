package de.shop.ui.main;

import de.shop.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import de.shop.ui.kunde.Kunden;

public class Main extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View view) {
		Intent intent = new Intent(this, Kunden.class);
		startActivity(intent);
	}

}
