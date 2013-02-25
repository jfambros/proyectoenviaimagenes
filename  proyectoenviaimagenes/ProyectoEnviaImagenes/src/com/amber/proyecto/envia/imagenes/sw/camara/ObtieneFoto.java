package com.amber.proyecto.envia.imagenes.sw.camara;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.amber.proyecto.envia.imagenes.sw.EnviaImagenSW;
import com.amber.proyecto.envia.imagenes.sw.R;

public class ObtieneFoto extends Activity{
	private ManejoFoto preview;
	Button buttonClick; // <2>

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.obtienefoto);

	    preview = new ManejoFoto(this); // <3>
	    ((FrameLayout) findViewById(R.id.preview)).addView(preview); // <4>

	    buttonClick = (Button) findViewById(R.id.btnObtieneFoto);
	    buttonClick.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) { // <5>
	        preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	      }
	    });

	  }

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
