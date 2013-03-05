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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.EnviaImagenSW;
import com.amber.proyecto.envia.imagenes.sw.Principal;
import com.amber.proyecto.envia.imagenes.sw.R;

public class ObtieneFoto extends Activity{
	private ManejoFoto preview;
	private FrameLayout frameLayout;
	private LocationManager milocManager;
	private LocationListener milocListener;
	private String nombreImagen;
	private double latitud;
	private double longitud;
	private String coordenadas;
	private Location locCoordenadas;
	private String ruta;
	private File archivo;
	private FileOutputStream archivoAlmacenado;

	  public Location getLocCoordenadas() {
		return locCoordenadas;
	}


	public void setLocCoordenadas(Location locCoordenadas) {
		this.locCoordenadas = locCoordenadas;
	}


	/** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.obtienefoto);

	    nombreImagen = "FT"+System.currentTimeMillis()+".jpg"; 
		ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +File.separator;
		
		archivo = new File(ruta,nombreImagen);

		
		
	    preview = new ManejoFoto(this); // <3>
	    frameLayout = ((FrameLayout) findViewById(R.id.preview));
	    frameLayout.addView(preview);// <4>
	    frameLayout.setOnClickListener(framePres);
	    
	    milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	  }	    
	    /*
	  @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		preview.getHolder().removeCallback(preview);
		preview.camera.stopPreview();
		preview.camera.release();
		preview.camera = null;
	}
	*/
	  private void obtieneCoordenadas(){
		  milocListener = new MiLocationListener();
		    milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);		  
	  }

	  private OnClickListener framePres = new OnClickListener() {
			
		public void onClick(View v) {
			
			preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			
			milocListener = new MiLocationListener();
		    milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);
		    
		    latitud = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
		    longitud = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		    try {
		    	Thread.sleep (3000);
		    	} catch (Exception e) {
		    	// Mensaje en caso de que falle
		    	}
			  //abrimos la actividad que envía la imagen
			  Intent intent = new Intent();
			  intent.setClass(ObtieneFoto.this, EnviaImagenSW.class);
			  intent.putExtra("ruta", ruta);
			  intent.putExtra("nombreImagen", nombreImagen);
			  Log.i("nombreImagen", ruta+nombreImagen);
			  intent.putExtra("latitud", latitud);
			  intent.putExtra("longitud", longitud);
			  //Log.i("coor;:", locCoordenadas.toString()+":");
			  
			  startActivity(intent);		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	      }
	    }
	  };	
	  
	  public class MiLocationListener implements LocationListener
	  {

		  public void onLocationChanged(Location loc)
		  {
		
			  loc.getLatitude();
			  loc.getLongitude();
			  //String coordenadas = "Mis coordenadas son: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
			  coordenadas = loc.getLatitude()+" "+loc.getLongitude();
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
	  
	  private void espera(){
		  
	  }
		
	  
}
	  

