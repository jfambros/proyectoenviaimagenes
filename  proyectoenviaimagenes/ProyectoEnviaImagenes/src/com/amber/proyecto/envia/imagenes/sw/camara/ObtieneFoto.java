package com.amber.proyecto.envia.imagenes.sw.camara;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.EnviaImagenSW;
import com.amber.proyecto.envia.imagenes.sw.R;

public class ObtieneFoto extends Activity{
	private ManejoFoto preview;
	private FrameLayout frameLayout;
	private LocationManager milocManager;
	private LocationListener milocListener;
	private String nombreImagen;

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.obtienefoto);

	    preview = new ManejoFoto(this); // <3>
	    frameLayout = ((FrameLayout) findViewById(R.id.preview));
	    frameLayout.addView(preview);// <4>
	    frameLayout.setOnClickListener(framePres);
	    
	    milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

	   
	    

	  }	    
	    
	
	  private void obtieneCoordenadas(){
		  milocListener = new MiLocationListener();
		    milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);		  
	  }

	  private OnClickListener framePres = new OnClickListener() {
			
		public void onClick(View v) {
			preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			obtieneCoordenadas();
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
	      FileOutputStream outStream = null;
	      try {
		        // Write to SD Card
		    	  long nombreImagen =System.currentTimeMillis(); 
		    	  outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",nombreImagen)); // <9>
		    	  outStream.write(data);
		    	  outStream.close();
		    	  //abrimos la actividad que envía la imagen
		    	  Intent intent = new Intent();
		    	  intent.setClass(ObtieneFoto.this, EnviaImagenSW.class);
		    	  intent.putExtra("nombreImagen", nombreImagen);
		    	  startActivity(intent);
	      } catch (FileNotFoundException e) { // <10>
	        e.printStackTrace();
	      } catch (IOException e) {
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
			  String coordenadas = "Mis coordenadas son: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
			  Toast.makeText( getApplicationContext(),coordenadas,Toast.LENGTH_LONG).show();
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

}
