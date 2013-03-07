package com.amber.proyecto.envia.imagenes.sw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;
import com.amber.proyecto.envia.imagenes.sw.utils.Categoria;
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
	private TextView tvLongitd;
	private ImageView imagen;
	private ImageView ivAtrasEnvia;
	private String HOST = Variables.HOST;
	private Spinner spinnCategorias;
    private ArrayList<Categoria> listaCategoria;
    private int idCat;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviaimagensw);
        
        //obtenemos el nombre de la imagen
        bundle = getIntent().getExtras();
        ruta = bundle.getString("ruta");        
        nombreImagen = bundle.getString("nombreImagen");
        latitud = bundle.getDouble("latitud");
        longitud = bundle.getDouble("longitud");
        
        
        tvLatitud = (TextView) findViewById(R.id.tvLatitud);
        tvLongitd = (TextView) findViewById(R.id.tvLongitud);
        
        tvLatitud.setText(latitud+" ");
        tvLongitd.setText(longitud+" ");
        
        imagen = (ImageView)findViewById(R.id.ivImagen);
        //nombreImagen = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/"+nombreImagen;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(ruta+nombreImagen+".jpg", options);
        
        /*
        final int THUMBNAIL_SIZE = 64;


        Float width = new Float(bm.getWidth());
        Float height = new Float(bm.getHeight());
        Float ratio = width/height;
        bm = Bitmap.createScaledBitmap(bm, (int)(THUMBNAIL_SIZE * ratio), THUMBNAIL_SIZE, false);
*/
        
        imagen.setImageBitmap(bm); 
        
        
        //http://pastebin.com/qRZDaiqp
        btnEnviar = (Button)findViewById(R.id.btnEnviarEI);
        btnEnviar.setOnClickListener(btnEnviarPres);
        
        ivAtrasEnvia = (ImageView)findViewById(R.id.ivAtrasEnvia);
        ivAtrasEnvia.setOnClickListener(ivAtrasEnviaPres);
        
        spinnCategorias = (Spinner)findViewById(R.id.spinnCategoria);
        obtieneCategorias();

        
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
			String SOAP_ACTION="capeconnect:servicios:serviciosPortType#recibeImagen"; 
			String METHOD_NAME = "recibeImagen";
			String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
			String URL = "http://"+HOST+"/pags/servicios.php";   
			
			int noOfChunks ;
			String base64String,str;
			
			try{
				Bitmap bitmapOrg = BitmapFactory.decodeFile(ruta+nombreImagen);
				//Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.nature);

						ByteArrayOutputStream bao = new ByteArrayOutputStream();

						bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);

						byte [] ba = bao.toByteArray();

						String ba1=Base64.encodeBytes(ba);
						
						Log.i("imagen", ba1);
						request = new SoapObject(NAMESPACE, METHOD_NAME); 
						//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
					    
						request.addProperty("imagen", ba1);
						request.addProperty("nombreIm", nombreImagen); 
						
					    
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						
						envelope.dotNet = false;
						
						envelope.setOutputSoapObject(request);

						HttpTransportSE aht = new HttpTransportSE(URL); 

						aht.call(SOAP_ACTION, envelope);
						
						SoapObject result =  (SoapObject) envelope.bodyIn;
		                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
		                
						Log.i("result",spResul.toString());
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
	};
	
	private void obtieneCategorias(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#obtieneCategorias"; 
		String METHOD_NAME = "obtieneCategorias";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		String URL = "http://"+Variables.HOST+"/pags/servicios.php";
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
}
