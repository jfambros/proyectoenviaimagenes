package com.amber.proyecto.envia.imagenes.sw;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;

public class Principal extends Activity {
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Button btnIniciar;
	private ImageView ivConecta;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		utilizarGPS();
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mensaje("Advertencia", "Debe activar el GPS para utilizar la aplicación");
		}
		else {
			 btnIniciar = (Button)findViewById(R.id.btnIniciaCamara);
			 locationListener = new LocationListener() {

			    public void onLocationChanged(Location location) {

			        //Remove the listener and make the button visible        
			        locationManager.removeUpdates(locationListener);
			        //btnIniciar.setVisibility(1);
			    	btnIniciar.setVisibility(1);
			    	btnIniciar.setOnClickListener(btnIniciarPres);
			    	ivConecta = (ImageView)findViewById(R.id.ivConectar);
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {}
			    public void onProviderEnabled(String provider) {}
			    public void onProviderDisabled(String provider) {}
			    
			};

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			/*
			if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() > 0){
				btnIniciar.setVisibility(1);
				btnIniciar.setOnClickListener(btnIniciarPres);
			}*/
		
		}
		
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		List<String> listaProviders = locationManager.getAllProviders();
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
