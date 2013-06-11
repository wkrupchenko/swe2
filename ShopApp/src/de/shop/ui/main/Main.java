package de.shop.ui.main;

import de.shop.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import de.shop.ui.artikel.ArtikelSuchenId;
import de.shop.ui.kunde.KundeSuchenId;

public class Main extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		findViewById(R.id.btn_suchen_kunde_id).setOnClickListener(this);
		findViewById(R.id.btn_suchen_artikel_id).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.btn_suchen_kunde_id){ 
			Intent intent = new Intent(this, KundeSuchenId.class);
			startActivity(intent);
		}
		else if(view.getId() == R.id.btn_suchen_artikel_id){ 
			Intent intent = new Intent(this, ArtikelSuchenId.class);
			startActivity(intent);
		}
	}

}
