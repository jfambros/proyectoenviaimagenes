package com.amber.proyecto.envia.imagenes.sw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

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
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;
import com.amber.proyecto.envia.imagenes.sw.mibd.BD;
import com.amber.proyecto.envia.imagenes.sw.utils.Conexiones;
import com.amber.proyecto.envia.imagenes.sw.utils.ContenidoArray;
import com.amber.proyecto.envia.imagenes.sw.utils.EnviaArchivoHttp;
import com.amber.proyecto.envia.imagenes.sw.utils.Imagen;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class Principal extends Activity {
	private LocationManager locationManager;
	private ImageView ivTomarFoto;
	private TextView tvTomarFoto;
	private TextView tvEnviaImagenes;
	private ImageView ivEnviaImagenes;
	private ImageView ivBuscaMapa;
	private ImageView ivRegistro;
	private SoapObject request;
	private String HOST = Variables.HOST;
	private String URL = "http://"+HOST+"/pags/servicios.php";
	private boolean gps_on;
	private boolean wifi_on;
	private Location loc;
	private MediaPlayer mediaPlayerSonido;
	private boolean activado = false;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		ivTomarFoto = (ImageView)findViewById(R.id.ivtomarFoto);
		tvTomarFoto = (TextView)findViewById(R.id.tvTomarFoto);
		
		ivEnviaImagenes = (ImageView)findViewById(R.id.ivAlmacenaBD);
		ivEnviaImagenes.setOnClickListener(ivEnviaImagenesPres);
		
		tvEnviaImagenes = (TextView)findViewById(R.id.tvEnviaImagenes);
		
		
		ivBuscaMapa = (ImageView)findViewById(R.id.ivBuscaMapa);
		ivBuscaMapa.setOnClickListener(ivBuscaMapaPres);
		
		ivRegistro = (ImageView)findViewById(R.id.ivRegistrar);
		ivRegistro.setOnClickListener(ivRegistroPres);
		
		tvEnviaImagenes.setText("Enviar imagen ("+verificaCantidad()+")");
		
		

		utilizarGPS();

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mensaje("Advertencia", "Debe activar el GPS para utilizar la aplicación");
		}
		else {

			 //http://stackoverflow.com/questions/2021176/how-can-i-check-the-current-status-of-the-gps-receiver
			 locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			 gps_on =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			 wifi_on = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		     request_updates();
		     if (loc != null){
		    	 mostrar();
		     }

		}
	
	}
	private void verificaInternetBD(){
		if (Conexiones.conexionInternet(this) == true && Conexiones.respondeServidor(URL) == true ){
			BD bd = new BD(this);
			int tot = bd.cuentaRegImagenes();
			if (tot > 0){
				for (int i=0;i<tot;i++){
					enviaImagenBD();
				}
				Toast.makeText(this, "Servidor encontrado, enviando imágenes!", Toast.LENGTH_LONG).show();
				tvEnviaImagenes.setText("Enviar imagen ("+verificaCantidad()+")");
				bd.close();
			}
			else{
				Toast.makeText(this, "No existen registros en la base de datos", Toast.LENGTH_LONG).show();
				bd.close();
			}
			

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
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // GPS is enabled on device so lets add a loopback for this locationmanager
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, locationListenerNetwork);
            Log.i("Network","Red encontrada");
        }   
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Each time the location is changed we assign loc
            loc = location;
		     if (loc != null){
		    	 mostrar();
		     }
        }

         // Need these even if they do nothing. Can't remember why.
         public void onProviderDisabled(String arg0) {}
         public void onProviderEnabled(String provider) {}
         public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
    
    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            loc = location;
            locationManager.removeUpdates(this);
            if (loc != null){
            	 mostrar();
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private OnClickListener ivBuscaMapaPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(Principal.this, Busca.class);
			startActivity(intent);
			//Principal.this.finish();
		}
	};
	
	private OnClickListener ivRegistroPres = new OnClickListener() {

		public void onClick(View v) {
			
		}
	};

	private OnClickListener ivIniciarPres = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(Principal.this,ObtieneFoto.class);
			intent.putExtra("latitud", loc.getLatitude());
			intent.putExtra("longitud", loc.getLongitude());
			startActivity(intent);
		}
	};
	
	private OnClickListener ivEnviaImagenesPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (verificaCantidad() == 0){
				Toast.makeText(Principal.this,"No hay imagenes en la base de datos", Toast.LENGTH_LONG).show();
			}
			verificaInternetBD();
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

		tvEnviaImagenes.setText("Enviar imagen ("+verificaCantidad()+")");
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mensaje("Advertencia", "Debe activar el GPS para utilizar la aplicación");
		}
		
		ivTomarFoto = (ImageView)findViewById(R.id.ivtomarFoto);
		tvTomarFoto = (TextView)findViewById(R.id.tvTomarFoto);
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, locationListenerNetwork);
	
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
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
	
	private void mostrar(){
		ivTomarFoto.setVisibility(1);
		tvTomarFoto.setVisibility(1);
		ivTomarFoto.setOnClickListener(ivIniciarPres);
	    if (activado == false){
	    	sonido("sonido");
	    	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    	v.vibrate(2000);
	    	activado = true;
	    }
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

	private void enviaImagenBD(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#recibeImagen"; 
		String METHOD_NAME = "recibeImagen";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		BD bd = new BD(this);
		
		SoapSerializationEnvelope envelope;
		HttpTransportSE aht;
		Imagen imagenes = new Imagen();
			
		try{
			request = new SoapObject(NAMESPACE, METHOD_NAME); 
			imagenes = bd.obtieneImagen();
			File verifica = new File(Variables.ruta+imagenes.getNombreImagen());
			if (verifica.exists()){
				request.addProperty("nombreImagen", imagenes.getNombreImagen());
				enviaImagenHttp(imagenes.getNombreImagen());
				bd.borraImagen(imagenes.getNombreImagen());
				request.addProperty("latitud", Double.toString(imagenes.getLatitud()));
				request.addProperty("longitud", Double.toString(imagenes.getLongitud()));
				request.addProperty("comentario", imagenes.getComentario());						
				request.addProperty("idCategoria", Integer.toString(imagenes.getIdCategoria()));
				request.addProperty("calificacion", Float.toString(imagenes.getCalificacion()));
				envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				
				envelope.dotNet = false;
										
				envelope.setOutputSoapObject(request);
				envelope.addMapping(NAMESPACE, "contenido", new ContenidoArray().getClass());

				aht = new HttpTransportSE(URL);

				aht.call(SOAP_ACTION, envelope);
				
				SoapObject result =  (SoapObject) envelope.bodyIn;
                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
                
                
				Log.i("resultado",spResul.toString());
				request = null;
				imagenes = null;
				System.gc();
				
	
				Toast.makeText(Principal.this, "¡Imágenes guardadas en el dispositivo enviadas!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(Principal.this, "¡No se encuentra el archivo! "+imagenes.getNombreImagen(), Toast.LENGTH_LONG).show();
			}
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
	
	private void enviaImagenHttp(String nombreImagen){
		try {
		    // Set your file path here
		    FileInputStream fstrm = new FileInputStream(Variables.ruta+nombreImagen);

		    // Set your server page url (and the file title/description)
		   EnviaArchivoHttp enviaArchivo = new EnviaArchivoHttp ("http://"+Variables.HOST+"/pags/recibeimagen.php", nombreImagen);
		    //new EnviaImagenHttp("http://"+Variables.HOST+"/pags/recibeimagen.php", nombreImagen).execute(fstrm);
		    enviaArchivo.envia(fstrm);
		    
		    fstrm.close();

		  } catch (FileNotFoundException e) {
		    // Error: File not found
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
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

	 OnCompletionListener completionList = new OnCompletionListener() {
         
         public void onCompletion(MediaPlayer mp) {
                 mediaPlayerSonido.release();
         }
 };
 
 
	private void sonido(String sSonido){

        int resIDSonido = getResources().getIdentifier(sSonido, "raw", getPackageName());
        if (mediaPlayerSonido != null){
                mediaPlayerSonido.release();
        }
        mediaPlayerSonido = null;
        mediaPlayerSonido = new MediaPlayer();
        mediaPlayerSonido = MediaPlayer.create(Principal.this, resIDSonido);
        mediaPlayerSonido.start();
        mediaPlayerSonido.setLooping(false);
        mediaPlayerSonido.setOnCompletionListener(completionList);
	}
	
	private int verificaCantidad(){
		BD bd = new BD(this);
		int cant = bd.cuentaRegImagenes();
		bd.close();
		return cant;


	}
		
}


