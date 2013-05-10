package com.amber.proyecto.envia.imagenes.sw.camara;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.EnviaImagenSW;
import com.amber.proyecto.envia.imagenes.sw.InicioSesion;
import com.amber.proyecto.envia.imagenes.sw.Principal;
import com.amber.proyecto.envia.imagenes.sw.R;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class ObtieneFoto extends Activity{
	private ManejoFoto preview;
	private FrameLayout frameLayout;
	private LocationManager milocManager;
	private String nombreImagen = "FT"+System.currentTimeMillis(); ;
	private double latitud;
	private double longitud;
	private Location coordenadas;
	private Location locCoordenadas;
	private Bundle bundle;
	private String ruta = Variables.ruta;
	private File archivo;
	private FileOutputStream archivoAlmacenado;
	private ImageView ivAtras;

	  public Location getLocCoordenadas() {
		return locCoordenadas;
	}


	public void setLocCoordenadas(Location locCoordenadas) {
		this.locCoordenadas = locCoordenadas;
	}

	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	            return false;
	        }
	        return super.onKeyDown(keyCode, event);
	    }

	/** Called when the activity is first created. */
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.obtienefoto);

	    //ivAtras = (ImageView)findViewById(R.id.ivAtrasFoto);
	    //ivAtras.setOnClickListener(ivAtrasPres);
	    bundle = getIntent().getExtras();

	    File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
	    if(!folder.exists()){
	      folder.mkdirs();
	    }
		 
		
		archivo = new File(ruta,nombreImagen+Variables.tipoArchivo);

		

			preview = new ManejoFoto(this); // <3>


		    frameLayout = ((FrameLayout) findViewById(R.id.preview));
		    frameLayout.addView(preview);// <4>
		    frameLayout.setOnClickListener(framePres);
		    
		    milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		    milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);	
	  }	    
	  
	  @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cierraCam();
	}

	  
	  private OnClickListener ivAtrasPres = new OnClickListener() {

		public void onClick(View v) {
			//cierraCam();
				Intent intent = new Intent();
			  intent.setClass(ObtieneFoto.this, Principal.class);
			  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  preview.camera.release();
			  startActivity(intent);
			
		}
	};
	 
	  private void cierraCam(){
			preview.getHolder().removeCallback(preview);
			preview.camera.stopPreview();
			//preview.camera.release();
			preview.camera = null;  
	  }
	  
	  
	  private LocationListener milocListener = new LocationListener() {

			public void onStatusChanged(String provider, int status, Bundle extras) {

				
			}

			public void onProviderEnabled(String provider) {

				
			}

			public void onProviderDisabled(String provider) {

				
			}

			public void onLocationChanged(Location location) {
				  //String coordenadas = "Mis coordenadas son: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
				  coordenadas = location;
				  if (coordenadas == null){
					  //latitud = bundle.getDouble("latitud");
					  //longitud = bundle.getDouble("longitud");
				  }
				
			}
		};
	  
	  private OnClickListener framePres = new OnClickListener() {
			
		public void onClick(View v) {
		    milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);
		    //milocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		    if (coordenadas != null){
		    	preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		    	Toast.makeText(ObtieneFoto.this, "Guardando imagen...", Toast.LENGTH_LONG).show();
			    try {
			    	Thread.sleep (2000);
			    	} catch (Exception e) {
			    	// Mensaje en caso de que falle
			    	}
			    finally{
			    	
					  //abrimos la actividad que env�a la imagen
			    	  cierraCam();
					  Intent intent = new Intent();
					  intent.setClass(ObtieneFoto.this, EnviaImagenSW.class);
					  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					  intent.putExtra("ruta", ruta);
					  intent.putExtra("nombreImagen", nombreImagen+Variables.tipoArchivo);
					  intent.putExtra("latitud", coordenadas.getLatitude());
					  intent.putExtra("longitud", coordenadas.getLongitude());
					  //Log.i("coor;:", locCoordenadas.toString()+":");
					  
					  startActivity(intent);
			    }
		    }
		    else{
		    	Toast.makeText(getApplicationContext(), "Se perdió la conexión GPS,  intente nuevamente",Toast.LENGTH_LONG).show();
		    }
		    	
		    
		    /*
		    if (milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null){
		    	milocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		    }
		    else{
		    	latitud = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			    longitud = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		    }

	    	if (milocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null){
	    		  Toast.makeText(ObtieneFoto.this, "Se perdió la señal gps,  utilizando la última ubicación", Toast.LENGTH_LONG).show();
				  latitud = bundle.getDouble("latitud");
				  longitud = bundle.getDouble("longitud");
	    	}
	    	else{
		    	latitud = milocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
			    longitud = milocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
	    	}
		    	*/
			    
	    	
		}
	  };
	  // Called when shutter is opened
	  ShutterCallback shutterCallback = new ShutterCallback() { // <6>
	    public void onShutter() {
	    }
	  };

	  // Handles data for raw picture
	  PictureCallback rawCallback = new PictureCallback() { // <7>
	    public void onPictureTaken(byte[] data, Camera camera) {
	    }
	  };

	  // Handles data for jpeg picture
	  PictureCallback jpegCallback = new PictureCallback() { // <8>
	    public void onPictureTaken(byte[] data, Camera camera) {
	      File outStream = null;
	      try {
		        // Write to SD Card
	    	  boolean mExternalStorageAvailable = false;
	    	  boolean mExternalStorageWriteable = false;
	    	  String state = Environment.getExternalStorageState();

	    	  if (Environment.MEDIA_MOUNTED.equals(state)) {
	    	      // We can read and write the media
	    		
	    	      mExternalStorageAvailable = mExternalStorageWriteable = true;
		    	  
			      archivoAlmacenado = new FileOutputStream(archivo);
			      archivoAlmacenado.write(data);
			      archivoAlmacenado.close();
			    	  //outStream.write(data);
			    	  //outStream.close();
	    	  }

	      } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			data = null;
			if (preview.camera !=null) {
	              //stop the preview
	              preview.camera.stopPreview();
	              //release the camera
	              preview.camera.release();
	              //unbind the camera from this object
	              preview.camera = null;
	          }
	      }
	    }
	  };
	  
	    LocationListener locationListenerNetwork = new LocationListener() {
	        public void onLocationChanged(Location location) {
	            milocManager.removeUpdates(this);
	            latitud = location.getLatitude();
	            longitud = location.getLongitude();

	        }
	        public void onProviderDisabled(String provider) {}
	        public void onProviderEnabled(String provider) {}
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	    };
	  
	  
	  public class MiLocationListener implements LocationListener
	  {

		  public void onLocationChanged(Location loc)
		  {
		
			  
			  //Toast.makeText( getApplicationContext(),"latitud: "+latitud+" long: "+longitud,Toast.LENGTH_LONG).show();

		  }
		  public void onProviderDisabled(String provider)
		  {
			  Toast.makeText( getApplicationContext(),"Gps Desactivado",Toast.LENGTH_SHORT ).show();
		  }
		  public void onProviderEnabled(String provider)
		  {
			  Toast.makeText( getApplicationContext(),"Gps Activo",Toast.LENGTH_SHORT ).show();
		  }
			  public void onStatusChanged(String provider, int status, Bundle extras){}
	  }
	  
	  private void mensaje(String titulo, String msj){
	        new AlertDialog.Builder(ObtieneFoto.this)
	        .setTitle(titulo)
	        .setMessage(msj)
	        .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		setResult(RESULT_OK);
	        	}
	        })
	        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					frameLayout.addView(preview);
					setResult(RESULT_CANCELED);
				}
			})
	        .show();   
		}
	  
		
	  
}
	  

