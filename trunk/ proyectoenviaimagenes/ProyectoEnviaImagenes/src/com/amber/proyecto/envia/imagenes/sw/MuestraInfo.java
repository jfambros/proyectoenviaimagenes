package com.amber.proyecto.envia.imagenes.sw;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.proyecto.envia.imagenes.sw.utils.ImagenParcelable;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class MuestraInfo extends Activity{
	private Bundle bundle;
	private ProgressDialog pDialog;
	private TextView tvMuestraInfo;
	private TextView tvCategoria;
	private ImageView ivAtrasMuestraInfo;
	private ImageView ivInicioMuestraInfo;
	private ImagenParcelable datosImagen = new ImagenParcelable();
	private String ruta = "http://"+Variables.HOST+"/pags/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muestrainfo);
		bundle = getIntent().getExtras();
		datosImagen = bundle.getParcelable("datosImagen");
		new MuestraImagenURL((ImageView)findViewById(R.id.ivImagenMuestraInfo)).execute(ruta+datosImagen.getNombreImagen());
		//new MuestraImagen((ImageView)findViewById(R.id.ivImagenMuestraInfo)).execute(ruta+datosImagen.getNombreImagen());
		
		tvCategoria = (TextView)findViewById(R.id.tvCategoriaMuestraInfo);
		tvCategoria.setText(tvCategoria.getText()+datosImagen.getNombreCategoria());
		
		tvMuestraInfo = (TextView)findViewById(R.id.tvComentarioMuestraInfo);
		tvMuestraInfo.setText(datosImagen.getComentario());
		
		ivAtrasMuestraInfo = (ImageView)findViewById(R.id.ivAtrasMuestraInfo1);
		ivAtrasMuestraInfo.setOnClickListener(ivAtrasMuestraPres);
		
		ivInicioMuestraInfo = (ImageView) findViewById(R.id.ivInicioMuestraInfo);
		ivInicioMuestraInfo.setOnClickListener(ivInicioMuestraPres);
			        
		//Log.i("nombreImagen ",datosImagen.getNombreImagen()+" "+datosImagen.getNombreCategoria());
	}
	
	private OnClickListener ivAtrasMuestraPres = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("imagenes", bundle.getParcelableArrayList("imagenes"));
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(MuestraInfo.this, Mapa.class);
    		startActivity(intent);
    		finish();			
		}
	};
	
	private OnClickListener ivInicioMuestraPres = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent();
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(MuestraInfo.this, Principal.class);
    		startActivity(intent);
    		finish();			
		}
	};
	
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case 0:
	        pDialog = new ProgressDialog(this);
	        pDialog.setMessage("Descargando imagen. Espere...");
	        pDialog.setIndeterminate(false);
	        pDialog.setMax(100);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        pDialog.setCancelable(true);
	        pDialog.show();
	        return pDialog;
	    default:
	        return null;
	    }
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	
	class MuestraImagenURL extends AsyncTask<String, String, Bitmap> {
		  private ImageView bmImage;

		  public MuestraImagenURL(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
				  BitmapFactory.Options options = new BitmapFactory.Options();
				     options.inSampleSize = 2;
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        InputStream in2 = new java.net.URL(urldisplay).openStream();
		        
		        
		        URL url = new URL(urls[0]);
		        URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	            int lenghtOfFile = conection.getContentLength();
	            
		        
		        byte data[] = new byte[1024];
		        int count = 0;
	            long total = 0;
	            while ((count = in.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
	                publishProgress(""+(int)((total*100)/lenghtOfFile));
	 
	            }
	            
	            mIcon11 = BitmapFactory.decodeStream(in2, null, options);
		        in.close();
		        in2.close();
		      } catch (Exception e) {
		          Log.e("Error: ", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }

		  protected void onPostExecute(Bitmap result) {
			  dismissDialog(0);   
		      bmImage.setImageBitmap(result);
		  }
		  
		  @Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog(0);
	
		}
		  
		  @Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			pDialog.setProgress(Integer.parseInt(values[0]));			
		}
		  
	}
	
}
