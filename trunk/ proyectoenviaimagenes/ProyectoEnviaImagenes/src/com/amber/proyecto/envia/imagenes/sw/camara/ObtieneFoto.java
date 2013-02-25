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

import com.amber.proyecto.envia.imagenes.sw.EnviaImagenSW;
import com.amber.proyecto.envia.imagenes.sw.R;
import com.google.android.maps.GeoPoint;

public class ObtieneFoto extends Activity{
	private ManejoFoto preview;
	private FrameLayout frameLayout;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private GeoPoint currentGeoPoint;

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.obtienefoto);

	    preview = new ManejoFoto(this); // <3>
	    frameLayout = ((FrameLayout) findViewById(R.id.preview));
	    frameLayout.addView(preview);// <4>
	    frameLayout.setOnClickListener(framePres);

	    
	    locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location arg0) {
				localizacion();
				
			}
		};
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	    localizacion();
	    
	  }
	  
	  public void localizacion(){
	    	setCurrentGeoPoint(new GeoPoint( 
	        		(int)(locationManager.getLastKnownLocation(
	        				LocationManager.GPS_PROVIDER).getLatitude()*1000000.0),
	        		(int)(locationManager.getLastKnownLocation(
	        				LocationManager.GPS_PROVIDER).getLongitude()*1000000.0)));
	    }
	  
		public void setCurrentGeoPoint(GeoPoint currentGeoPoint) {
			this.currentGeoPoint = currentGeoPoint;
		}

		public GeoPoint getCurrentGeoPoint() {
			return currentGeoPoint;
		}

	  
	  
	  private OnClickListener framePres = new OnClickListener() {
			
		public void onClick(View v) {
			preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
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
}
