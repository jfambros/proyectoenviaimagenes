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
import com.amber.proyecto.envia.imagenes.sw.utils.Conexiones;
import com.amber.proyecto.envia.imagenes.sw.utils.Imagen;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class Principal extends Activity {
	private LocationManager locationManager;
	private Button btnIniciar;
	//private ImageView ivConecta;
	private SoapObject request;
	private String HOST = Variables.HOST;
	private String URL = "http://"+HOST+"/pags/servicios.php";
	private boolean gps_on;
	private Location loc;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		 btnIniciar = (Button)findViewById(R.id.btnIniciaCamara);
		verificaInternetBD();
		utilizarGPS();
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mensaje("Advertencia", "Debe activar el GPS para utilizar la aplicación");
		}
		else {

			 //http://stackoverflow.com/questions/2021176/how-can-i-check-the-current-status-of-the-gps-receiver
			 locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			 gps_on =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			 //loc = null;
		     request_updates();
		     if (loc != null){
		    	 btnIniciar.setVisibility(1);
			    	btnIniciar.setOnClickListener(btnIniciarPres);
		     }
/*
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

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);
			
			if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() > 0){
				btnIniciar.setVisibility(1);
				btnIniciar.setOnClickListener(btnIniciarPres);
			}
	*/	
		}
	
	}
	private void verificaInternetBD(){
		if (Conexiones.conexionInternet(this) == true && Conexiones.respondeServidor(URL) == true ){
			BD bd = new BD(this);
			if (bd.cuentaRegImagenes() >0 ){
			
				enviaImagenBD();
				Toast.makeText(this, "Servidor encontrado, enviando imágenes!", Toast.LENGTH_LONG).show();
			}
			
			bd.close();
		}
		else{
			Toast.makeText(this, "Servidor no se encontró", Toast.LENGTH_LONG).show();
		}
		
	}
	private void request_updates() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is enabled on device so lets add a loopback for this locationmanager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);
            Log.i("Gps","encontrado GPS");
        }      
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Each time the location is changed we assign loc
            loc = location;
		     if (loc != null){
		    	 btnIniciar.setVisibility(1);
			     btnIniciar.setOnClickListener(btnIniciarPres);
		     }
        }

         // Need these even if they do nothing. Can't remember why.
         public void onProviderDisabled(String arg0) {}
         public void onProviderEnabled(String provider) {}
         public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
	
	
	
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
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("On restart", "restaurando");
		verificaInternetBD();
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mensaje("Advertencia", "Debe activar el GPS para utilizar la aplicación");
		}
		
		btnIniciar = (Button)findViewById(R.id.btnIniciaCamara);
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);
		/*
		if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() > 0){
			btnIniciar.setVisibility(1);
			btnIniciar.setOnClickListener(btnIniciarPres);
		}
		*/			
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
	private boolean verificaRegBD(){
		BD bd = new BD(this);
		if (bd.cuentaRegImagenes() > 0 ){
			bd.close();
			return true;
		}
		else{
			bd.close();
			return false;
		}
	}
	private void enviaImagenBD(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#enviaImagen"; 
		String METHOD_NAME = "enviaImagen";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		ArrayList<Imagen> imagenes = new ArrayList<Imagen>();
		BD bd = new BD(this);
		
		if (bd.cuentaRegImagenes() > 0){
			imagenes = bd.obtieneImagenes();
			
			try{
				
					for (int i = 0; i<imagenes.size(); i++){
						request = new SoapObject(NAMESPACE, METHOD_NAME); 
						request.addProperty("nombreImagen", imagenes.get(i).getNombreImagen());
						request.addProperty("contenido", imagenes.get(i).getContenidoImagen());
						request.addProperty("latitud", Double.toString(imagenes.get(i).getLatitud()));
						request.addProperty("longitud", Double.toString(imagenes.get(i).getLongitud()));
						request.addProperty("comentario", imagenes.get(i).getComentario());						
						request.addProperty("categoria", imagenes.get(i).getIdCategoria());							

					    
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						
						envelope.dotNet = false;
						

						
						envelope.setOutputSoapObject(request);
	
						HttpTransportSE aht = new HttpTransportSE(URL); 
	
						aht.call(SOAP_ACTION, envelope);
						
						SoapObject result =  (SoapObject) envelope.bodyIn;
		                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
		                
		                bd.borraImagen(imagenes.get(i).getNombreImagen());
		                
						Log.i("resultado",spResul.toString());
						
					}
					Toast.makeText(Principal.this, "Imágenes guardadas en el dispositivo enviadas", Toast.LENGTH_LONG).show();
			    }
		
		    catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally{
				
			}
			
			
		}
		
		bd.close();
	}
	
	private void insertaCategoriasInternet(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#obtieneCategorias"; 
		String METHOD_NAME = "obtieneCategorias";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		SoapSerializationEnvelope envelope;
        HttpTransportSE httpt;
        BD bd = new BD(this);
     
        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
               
        httpt = new HttpTransportSE(URL);
        envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11 );
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);
        httpt.call(SOAP_ACTION, envelope);
        SoapObject result2 =  (SoapObject) envelope.getResponse();

        for(int cont=0; cont< result2.getPropertyCount(); cont ++){
        	SoapObject resultados = (SoapObject) result2.getProperty(cont);
        	//primitivas
        	SoapPrimitive idCategoria = (SoapPrimitive) resultados.getProperty("idCategoria");
        	SoapPrimitive nombreCategoria = (SoapPrimitive) resultados.getProperty("nombreCategoria");
        	bd.insertaCategoria(Integer.parseInt(idCategoria.toString()), nombreCategoria.toString());
        }
     }
     catch(Exception err){
    	 
     }
     bd.close();
	}

		
}
