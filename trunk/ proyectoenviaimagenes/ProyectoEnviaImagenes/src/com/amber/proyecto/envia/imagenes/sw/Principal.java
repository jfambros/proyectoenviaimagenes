package com.amber.proyecto.envia.imagenes.sw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;
import com.amber.proyecto.envia.imagenes.sw.mibd.BD;
import com.amber.proyecto.envia.imagenes.sw.utils.Imagen;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class Principal extends Activity {
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Button btnIniciar;
	private ImageView ivConecta;
	private SoapObject request;
	private String HOST = Variables.HOST;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		if (conexionInternet() == true){
			enviaImagenBD();
		}
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
	
	private boolean conexionInternet() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}
	/*
	private ArrayList<Imagen> obtieneValoresBD(){
		BD bd = new BD(Principal.this);
		SQLiteDatabase sqLite = bd.getReadableDatabase();
		ArrayList<Imagen> imagenes = new ArrayList<Imagen>();
		imagenes = bd.obtieneImagenes();
		bd.close();
		return imagenes;
	}
	*/
	private void enviaImagenBD(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#enviaImagen"; 
		String METHOD_NAME = "enviaImagen";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		String URL = "http://"+HOST+"/pags/servicios.php";
		ArrayList<Imagen> imagenes = new ArrayList<Imagen>();
		BD bd = new BD(Principal.this);
		if (bd.cuentaRegImagenes() != 0){
			imagenes = bd.obtieneImagenes();
			try{
				
					for (int i = 0; i<imagenes.size(); i++){
						request = new SoapObject(NAMESPACE, METHOD_NAME); 
						request.addProperty("nombreImagen", imagenes.get(i).getNombreImagen());
						request.addProperty("contenido", imagenes.get(i).getContenidoImagen());
						request.addProperty("latitud", imagenes.get(i).getLatitud());
						request.addProperty("longitud", imagenes.get(i).getLongitud());
						request.addProperty("comentario", imagenes.get(i).getComentario());
						request.addProperty("categoria", imagenes.get(i).getIdCategoria());					
					    
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						
						envelope.dotNet = false;
						
						envelope.setOutputSoapObject(request);
	
						HttpTransportSE aht = new HttpTransportSE(URL); 
	
						aht.call(SOAP_ACTION, envelope);
						
						SoapObject result =  (SoapObject) envelope.bodyIn;
		                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
		                
						//Log.i("result",spResul.toString());
						
					}
					Toast.makeText(Principal.this, "Imagen enviada", Toast.LENGTH_LONG).show();
			    } 
		    catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally{
				
			}
			bd.borraImagenes();

		}
		bd.close();
	}
	
		
}
