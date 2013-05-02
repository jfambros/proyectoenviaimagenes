package com.amber.proyecto.envia.imagenes.sw;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
import android.widget.RatingBar;
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
	private ImageView ivBuscaMuestraInfo;
	private ImageView ivImagen;
	private RatingBar rbMuestraInfo;
	private ImagenParcelable datosImagen = new ImagenParcelable();
	private String ruta = "http://"+Variables.HOST+"/pags/";
    private String URL = "http://"+Variables.HOST+"/pags/servicios.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muestrainfo);
		bundle = getIntent().getExtras();
		datosImagen = bundle.getParcelable("datosImagen");
		ivImagen = (ImageView)findViewById(R.id.ivImagenMuestraInfo);
		new MuestraImagenURL(ivImagen).execute(ruta+datosImagen.getNombreImagen());
		//new MuestraImagen((ImageView)findViewById(R.id.ivImagenMuestraInfo)).execute(ruta+datosImagen.getNombreImagen());
		System.gc();
		tvCategoria = (TextView)findViewById(R.id.tvCategoriaMuestraInfo);
		tvCategoria.setText(tvCategoria.getText()+datosImagen.getNombreCategoria());
		
		tvMuestraInfo = (TextView)findViewById(R.id.tvComentarioMuestraInfo);
		tvMuestraInfo.setText(datosImagen.getComentario());
		
		ivAtrasMuestraInfo = (ImageView)findViewById(R.id.ivAtrasMuestraInfo1);
		ivAtrasMuestraInfo.setOnClickListener(ivAtrasMuestraPres);
		
		ivInicioMuestraInfo = (ImageView) findViewById(R.id.ivInicioMuestraInfo);
		ivInicioMuestraInfo.setOnClickListener(ivInicioMuestraPres);
		
		ivBuscaMuestraInfo = (ImageView)findViewById(R.id.ivBuscaMuestraInfo1);
		ivBuscaMuestraInfo.setOnClickListener(ivBuscaMuestraInfoPres);
			        
		rbMuestraInfo = (RatingBar)findViewById(R.id.ratingBarMuestraInfo);
		rbMuestraInfo.setRating(datosImagen.getCalificacion());
		//Log.i("nombreImagen ",datosImagen.getNombreImagen()+" "+datosImagen.getNombreCategoria());
	}
	
	private OnClickListener ivBuscaMuestraInfoPres = new OnClickListener() {

		public void onClick(View v) {
			limpia();
			Intent intent = new Intent();
			intent.putExtra("queryFinal", bundle.getString("queryFinal"));
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(MuestraInfo.this, Busca.class);
    		startActivity(intent);
    		finish();						
		}
	};
	
	private OnClickListener ivAtrasMuestraPres = new OnClickListener() {

		public void onClick(View v) {
			limpia();
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("imagenes", obtieneImagenesSW());
			intent.putExtra("queryFinal", bundle.getString("queryFinal"));
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(MuestraInfo.this, Mapa.class);
    		startActivity(intent);
    		finish();			
		}
	};
	
	private OnClickListener ivInicioMuestraPres = new OnClickListener() {

		public void onClick(View v) {
			limpia();
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
	
	private void limpia(){

		ivImagen = null;
		System.gc();		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	
	class MuestraImagenURL extends AsyncTask<String, String, WeakReference<Bitmap>> {
		  private ImageView bmImage;

		  public MuestraImagenURL(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected WeakReference<Bitmap> doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      WeakReference<Bitmap> mIcon11 = null;
		      try {
				  BitmapFactory.Options options = new BitmapFactory.Options();
				     options.inSampleSize = 2;
				     options.inPurgeable=true;
				     options.inTempStorage =new byte[32 * 1024]; 
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
	            
	            mIcon11 = new WeakReference<Bitmap>(BitmapFactory.decodeStream(in2, null, options));
		        in.close();
		        in2.close();
		      } catch (Exception e) {
		          Log.e("Error: ", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }

		  protected void onPostExecute(WeakReference<Bitmap> result) {
			  dismissDialog(0);   
		      bmImage.setImageBitmap(result.get());
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
	
	
	private ArrayList<ImagenParcelable> obtieneImagenesSW(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#obtieneImagenes"; 
		String METHOD_NAME = "obtieneImagenes";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		
		SoapSerializationEnvelope envelope;
        HttpTransportSE httpt;
        ArrayList<ImagenParcelable> imagenParcelable = new ArrayList<ImagenParcelable>();
        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("opciones", bundle.getString("queryFinal"));
            httpt = new HttpTransportSE(URL);
            envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11 );
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);
            httpt.call(SOAP_ACTION, envelope);

            SoapObject resultado =  (SoapObject) envelope.getResponse();
            if (resultado.getPropertyCount() != 0){
	            for(int cont=0; cont< resultado.getPropertyCount(); cont ++){
	            	SoapObject resultados = (SoapObject) resultado.getProperty(cont);
	            	//primitivas
	            	SoapPrimitive nombreImagen = (SoapPrimitive) resultados.getProperty("nombreImagen");
	            	SoapPrimitive latitud = (SoapPrimitive) resultados.getProperty("latitud");
	            	SoapPrimitive longitd = (SoapPrimitive) resultados.getProperty("longitud");
	            	SoapPrimitive idCategoria = (SoapPrimitive) resultados.getProperty("idCategoria");
	            	SoapPrimitive comentario = (SoapPrimitive) resultados.getProperty("comentario");
	            	SoapPrimitive nombreCategoria = (SoapPrimitive) resultados.getProperty("nombreCategoria");
	            	SoapPrimitive calificacion = (SoapPrimitive) resultados.getProperty("calificacion");
	            	
	            	Log.i("Datos: ", cont+" "+nombreImagen.toString()+" "+latitud.toString()+" "+longitd.toString()+" "+idCategoria.toString()+" "+comentario.toString());
	            	ImagenParcelable ip = new ImagenParcelable();
	            	ip.setNombreImagen(nombreImagen.toString());
	            	ip.setLatitud(Double.parseDouble(latitud.toString()));
	            	ip.setLongitud(Double.parseDouble(longitd.toString()));
	            	ip.setIdCategoria(Integer.parseInt(idCategoria.toString()));
	            	ip.setComentario(comentario.toString());
	            	ip.setNombreCategoria(nombreCategoria.toString());
	            	ip.setCalificacion(Float.parseFloat(calificacion.toString()));
	            	
	            	imagenParcelable.add(ip);
	         }
            } 
        }
        catch (Exception err){
        	Log.e("Error", err.toString());
        }
        
        return imagenParcelable;
	}
	
}
