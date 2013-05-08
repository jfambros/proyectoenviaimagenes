package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class MuestraImagen extends AsyncTask<String, Void, Bitmap> {
	  private ImageView bmImage;

	  public MuestraImagen(ImageView bmImage) {
	      this.bmImage = bmImage;
	  }

	  protected Bitmap doInBackground(String... urls) {
	      String urldisplay = urls[0];
	      Bitmap mIcon11 = null;
	      try {
			  BitmapFactory.Options options = new BitmapFactory.Options();
			     options.inSampleSize = 2;
	        InputStream in = new java.net.URL(urldisplay).openStream();

	        mIcon11 = BitmapFactory.decodeStream(in, null, options);
	        in.close();
	      } catch (Exception e) {
	          Log.e("Error", e.getMessage());
	          e.printStackTrace();
	      }
	      return mIcon11;
	  }

	  protected void onPostExecute(Bitmap result) {

		   
	      bmImage.setImageBitmap(result);
	  }
	  
	  @Override
	protected void onPreExecute() {

		super.onPreExecute();
		
	}
	  
}
