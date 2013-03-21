package com.amber.proyecto.envia.imagenes.sw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;
import com.amber.proyecto.envia.imagenes.sw.mibd.BD;
import com.amber.proyecto.envia.imagenes.sw.utils.BitMapLoader;
import com.amber.proyecto.envia.imagenes.sw.utils.Categoria;
import com.amber.proyecto.envia.imagenes.sw.utils.Conexiones;
import com.amber.proyecto.envia.imagenes.sw.utils.DivideImagen;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class EnviaImagenSW extends Activity{
	private Button btnEnviar;
	private SoapObject request;
	private Bundle bundle;
	private String ruta; 
	private String nombreImagen;
	private double latitud;
	private double longitud;
	private TextView tvLatitud;
	private TextView tvLongitud;
	private String imagenCodificada;
	private ImageView imagen;
	private ImageView ivAtrasEnvia;
	private EditText etComentario;
	private String HOST = Variables.HOST;
	private int tamanio = Variables.tamArreglo;
	private Spinner spinnCategorias;
    private ArrayList<Categoria> listaCategoria;
    private int idCat;
    private String partes[] = new String[tamanio];
    private String URL = "http://"+HOST+"/pags/servicios.php";
    private BD bd;
    private int imagenW;
    private int imagenH;
    private Bitmap bm ;
    
    private int navid = R.id.ivImagen;

	
//http://androidactivity.wordpress.com/2011/09/24/solution-for-outofmemoryerror-bitmap-size-exceeds-vm-budget/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviaimagensw);

        
        bd = new BD(getBaseContext());
        nombreImagen = null;
        //obtenemos el nombre de la imagen
        
        bundle = getIntent().getExtras();
        ruta = bundle.getString("ruta");        
        nombreImagen = bundle.getString("nombreImagen");
        latitud = bundle.getDouble("latitud");
        longitud = bundle.getDouble("longitud");
        //codificaImagen();
        
        
        tvLatitud = (TextView) findViewById(R.id.tvLatitud);
        tvLongitud = (TextView) findViewById(R.id.tvLongitud);
        
        tvLatitud.setText(latitud+" ");
        tvLongitud.setText(longitud+" ");
        
       
        imagen = (ImageView)findViewById(R.id.ivImagen);
        //nombreImagen = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/"+nombreImagen;
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        
        options.inSampleSize = 2;
        bm = BitmapFactory.decodeFile(ruta+nombreImagen+".jpg", options);
        
        imagen.setImageBitmap(BitMapLoader.loadBitmap(ruta+nombreImagen+".jpg", 100, 100));
        /*
        bm2 = BitmapFactory.decodeFile(ruta+nombreImagen+".jpg");
        
        imagenW = bm2.getWidth();
        imagenH = bm2.getHeight();
        bm2.recycle();
        */
        //Log.i("width, heigth",imagenW+" "+imagenH);
        
        ivAtrasEnvia = (ImageView)findViewById(R.id.ivAtrasEnvia);
        ivAtrasEnvia.setOnClickListener(ivAtrasEnviaPres);
        
        spinnCategorias = (Spinner)findViewById(R.id.spinnCategoria);

        //http://pastebin.com/qRZDaiqp
        btnEnviar = (Button)findViewById(R.id.btnEnviarEI);
        btnEnviar.setOnClickListener(btnEnviarPres);
		etComentario = (EditText)findViewById(R.id.etComentario);
        
   
    	//obtenerDireccion();    
        if (Conexiones.conexionInternet(this) == true && Conexiones.respondeServidor(URL) == true){
            obtieneCategorias();
        }else{
        	categoriasSinInternet();
        }
        	
        
        
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	//liberaBM();
    	System.gc();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	imagenCodificada = null;
    }
    
	
    private OnClickListener ivAtrasEnviaPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(EnviaImagenSW.this, ObtieneFoto.class);
			startActivity(intent);
			
		}
	};

    private OnClickListener btnEnviarPres = new OnClickListener() {
		
		public void onClick(View v) {

			if (Conexiones.conexionInternet(EnviaImagenSW.this) == true && Conexiones.respondeServidor(URL) == true){
				codificaImagenInternet();
				enviaImagen();
			}
			else{
				Toast.makeText(EnviaImagenSW.this, "No hay conexión a internet, la imagen se guardará en el dispositivo", Toast.LENGTH_LONG).show();
				codificaImagenSinInternet();
				bd.insertaImagen(nombreImagen, latitud, longitud, idCat, etComentario.getText().toString());
				bd.insertaContenido(nombreImagen, partes);
				bd.close();
				Toast.makeText(EnviaImagenSW.this, "Imagen guardada en el dispositivo!", Toast.LENGTH_LONG).show();
			}
			mensaje("Aviso", "¿Qué deseas realizar?");
		}
	};
	
	private void liberaBM(){
        imagen.setImageBitmap(null);
        bm.recycle();

	}
	private void categoriasSinInternet(){
    	listaCategoria = new ArrayList<Categoria>();
    	listaCategoria = bd.obtieneCategorias();
    	bd.close();
    	ArrayList<String> aLCategorias = new ArrayList<String>();
    	for (int i= 0; i<listaCategoria.size(); i++){
    		aLCategorias.add(listaCategoria.get(i).getNombreCategoria());
    	}
    	
    	ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>( 
                this,
                android.R.layout.simple_spinner_item, aLCategorias
                 );
        adapterCategoria.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
            spinnCategorias.setAdapter(adapterCategoria);
                spinnCategorias.setSelection(0);
            spinnCategorias.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, 
                            View view, 
                            int position, 
                            long id) {
                        Categoria cat = listaCategoria.get(position);
                        //Log.i("Pais seleccionado", d.getNombrePais());
                        idCat = cat.getIdCategoria();
                        Log.i("categoria seleccionada", Integer.toString(cat.getIdCategoria()));
                    }

                                        public void onNothingSelected(AdapterView<?> arg0) {
                                        
                                        }
                }
            );
	}

	
	private void obtieneCategorias(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#obtieneCategorias"; 
		String METHOD_NAME = "obtieneCategorias";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		
		SoapSerializationEnvelope envelope;
        HttpTransportSE httpt;
        SoapObject result;


        ArrayList<String> nombresCategorias;
        
        
        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
               
        httpt = new HttpTransportSE(URL);
        envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11 );
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);
        httpt.call(SOAP_ACTION, envelope);
        result = (SoapObject)envelope.bodyIn;
        SoapObject result2 =  (SoapObject) envelope.getResponse();
        listaCategoria = new ArrayList<Categoria>();
        nombresCategorias = new ArrayList<String>();
        for(int cont=0; cont< result2.getPropertyCount(); cont ++){
        	SoapObject resultados = (SoapObject) result2.getProperty(cont);
        	//primitivas
        	SoapPrimitive idCategoria = (SoapPrimitive) resultados.getProperty("idCategoria");
        	SoapPrimitive nombreCategoria = (SoapPrimitive) resultados.getProperty("nombreCategoria");

        	Categoria c = new Categoria();
                 c.setIdCategoria(Integer.parseInt(idCategoria.toString()));
                 c.setNombreCategoria(nombreCategoria.toString());
                 listaCategoria.add(c);
                 nombresCategorias.add(nombreCategoria.toString());
     }
        
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>( 
                this,
                android.R.layout.simple_spinner_item,
                nombresCategorias );
        adapterCategoria.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
            spinnCategorias.setAdapter(adapterCategoria);
                spinnCategorias.setSelection(0);
            spinnCategorias.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, 
                            View view, 
                            int position, 
                            long id) {
                        Categoria cat = listaCategoria.get(position);
                        //Log.i("Pais seleccionado", d.getNombrePais());
                        idCat = cat.getIdCategoria();
                        Log.i("categoria seleccionada", Integer.toString(cat.getIdCategoria()));
                    }

                                        public void onNothingSelected(AdapterView<?> arg0) {
                                        
                                        }
                }
            );
            
    }
    catch(Exception err){
        Log.e("Error en llena paises", err.toString());
    }
        
		
		
	}
	
	private void codificaImagenInternet(){
		Bitmap bitmapOrg = BitmapFactory.decodeFile(ruta+nombreImagen+".jpg");
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 80, bao);
		byte [] ba = bao.toByteArray();
		imagenCodificada = Base64.encodeBytes(ba);
	}
	
	private void codificaImagenSinInternet(){
		DivideImagen divide = new DivideImagen(ruta+nombreImagen);
		partes = divide.divideBitmapArr(tamanio);

		/*
		ArrayList<Bitmap> arregloBM = new ArrayList<Bitmap>();
		DivideImagen divide = new DivideImagen(ruta+nombreImagen);
		arregloBM = divide.divideBitmap(tamanio);
		for (int i=0; i<arregloBM.size(); i++){
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			arregloBM.get(i).compress(Bitmap.CompressFormat.JPEG, 90, bao);
			byte [] ba = bao.toByteArray();
			partes[i] = Base64.encodeBytes(ba);
			arregloBM.get(i).recycle();
			//Log.i("Parte # "+i,partes[i]);
		}
		arregloBM.clear();	
		arregloBM = null;
		*/
	}
	
	private void enviaImagen(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#enviaImagen"; 
		String METHOD_NAME = "enviaImagen";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		try{
					request = new SoapObject(NAMESPACE, METHOD_NAME); 
					request.addProperty("nombreImagen", nombreImagen);
					request.addProperty("contenido", imagenCodificada);
					request.addProperty("latitud", tvLatitud.getText().toString());
					request.addProperty("longitud", tvLongitud.getText().toString());
					request.addProperty("comentario", etComentario.getText().toString());
					request.addProperty("categoria", idCat);					
				    
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					
					envelope.dotNet = false;
					
					envelope.setOutputSoapObject(request);

					HttpTransportSE aht = new HttpTransportSE(URL); 

					aht.call(SOAP_ACTION, envelope);
					
					SoapObject result =  (SoapObject) envelope.bodyIn;
	                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
	                
					Log.i("resultado",spResul.toString());
					Toast.makeText(EnviaImagenSW.this, "Imagen enviada", Toast.LENGTH_LONG).show();
		    } 
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			
		}
		
	}
	
	public static JSONObject getLocationInfo(String address) {
	    StringBuilder stringBuilder = new StringBuilder();
	    try {

	    address = address.replaceAll(" ","%20");    

	    HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    stringBuilder = new StringBuilder();


	        response = client.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    return jsonObject;
	}	
	
	private void obtenerDireccion(){
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this);
		try {
			addresses = geocoder.getFromLocation(latitud, longitud, 1);
			String address = addresses.get(0).getAddressLine(0);
			String city = addresses.get(0).getAddressLine(1);
			String country = addresses.get(0).getAddressLine(2);
			Toast.makeText(this, "Geocoder:"+address+" "+city+" "+country, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	/*//grabar nombre imagen
private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = 
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
    File image = File.createTempFile(
        imageFileName, 
        JPEG_FILE_SUFFIX, 
        getAlbumDir()
    );
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
}	 * 
	 
	 */
	

	
	
	private void mensaje(String titulo, String msj){
        new AlertDialog.Builder(EnviaImagenSW.this)
        .setTitle(titulo)
        .setMessage(msj)
        .setCancelable(false)
        .setPositiveButton("Tomar otra foto", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
    			liberaBM();
        		Intent intent = new Intent();
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		intent.setClass(EnviaImagenSW.this, ObtieneFoto.class);
        		startActivity(intent);
        		finish();
        	}
        })
        .setNeutralButton("Ir a inicio", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				liberaBM();
        		Intent intent = new Intent();
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		intent.setClass(EnviaImagenSW.this, Principal.class);
        		startActivity(intent);
        		setResult(RESULT_OK);				
			}
		})
		
        .show();   
	}
}
