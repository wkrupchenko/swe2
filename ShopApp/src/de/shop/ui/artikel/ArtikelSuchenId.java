package de.shop.ui.artikel;

import static de.shop.util.Konstanten.ARTIKEL_KEY;
import de.shop.R;
import de.shop.data.artikel.Artikel;
import de.shop.service.artikel.ArtikelService;
import de.shop.service.artikel.ArtikelService.ArtikelServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ArtikelSuchenId extends Activity implements OnClickListener {
	private static final String LOG_TAG = ArtikelSuchenId.class.getSimpleName();
	
	private ArtikelServiceBinder serviceBinder;
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			 ArtikelSuchenId.this.serviceBinder = (ArtikelServiceBinder) serviceBinder;
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) { }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suche_artikel_id);
		findViewById(R.id.btn_suchen).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		EditText artikelIdTxt = (EditText) findViewById(R.id.artikel_id);
		String artikelIdStr = artikelIdTxt.getText().toString();

		Long artikelId = Long.valueOf(artikelIdStr);
		Artikel artikel = serviceBinder.getArtikel(artikelId);
				
		final Intent intent = new Intent(view.getContext(), ArtikelDetails.class);
		intent.putExtra(ARTIKEL_KEY, artikel);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		Intent intent = new Intent(this, ArtikelService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		super.onStart();
	}	
	
	@Override
	protected void onStop() {
		unbindService(serviceConnection);
		super.onStop();
	}
}