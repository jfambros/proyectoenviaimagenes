package com.amber.proyecto.envia.imagenes.sw;

import java.util.List;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Principal extends Activity {
	private LocationManager locManager;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		utilizarGPS();
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mensaje("Advertencia", "Debe activar el GPS para utilizar la aplicación");
		}
		else {
			Button btnIniciar = (Button)findViewById(R.id.btnIniciaCamara);
			btnIniciar.setOnClickListener(btnIniciarPres);
		}
		
	}

	private OnClickListener btnIniciarPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(Principal.this,ObtieneFoto.class);
			startActivity(intent);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principal, menu);
		return true;
	}
	
	private void utilizarGPS(){
		locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		List<String> listaProviders = locManager.getAllProviders();
		Log.i("Prov", listaProviders.get(0).toString());
	}
	private void activarGPS(){
	       Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	       settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	       startActivity(settingsIntent);		
	}
	
	private void mensaje(String titulo, String msj){
        new AlertDialog.Builder(Principal.this)
        .setTitle(titulo)
        .setMessage(msj)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		activarGPS();
        		setResult(RESULT_OK);
        	}
        })
        .show();   
	}
	

}
