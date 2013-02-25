package com.amber.proyecto.envia.imagenes.sw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EnviaImagenSW extends Activity{
	private Button btnEnviar;
	private SoapObject request;
	private Bundle bundle;
	private String nombreImagen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviaimagensw);
        
        //obtenemos el nombre de la imagen
        bundle = getIntent().getExtras();
        nombreImagen = bundle.getString("nombreImagen");
        //http://pastebin.com/qRZDaiqp
        btnEnviar = (Button)findViewById(R.id.btnEnviarEI);
        btnEnviar.setOnClickListener(btnEnviarPres);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }
    
	
	

    private OnClickListener btnEnviarPres = new OnClickListener() {
		
		public void onClick(View v) {
			String SOAP_ACTION="capeconnect:servicios:serviciosPortType#recibeImagen"; 
			String METHOD_NAME = "recibeImagen";
			String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
			String URL = "http://10.0.2.2/pags/servicios.php";   
			
			int noOfChunks ;
			String base64String,str;
			
			try{
				Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.nature);

						ByteArrayOutputStream bao = new ByteArrayOutputStream();

						bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);

						byte [] ba = bao.toByteArray();

						String ba1=Base64.encodeBytes(ba);
						
						Log.i("imagen", ba1);
						request = new SoapObject(NAMESPACE, METHOD_NAME); 
						String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
					    
						request.addProperty("imagen", ba1);
						request.addProperty("nombreIm", timeStamp); 
						Log.i("nombre: ", timeStamp);
					    
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						
						envelope.dotNet = false;
						
						envelope.setOutputSoapObject(request);

						HttpTransportSE aht = new HttpTransportSE(URL); 

						aht.call(SOAP_ACTION, envelope);
						
						SoapObject result =  (SoapObject) envelope.bodyIn;
		                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
		                
						Log.i("result",spResul.toString());

					
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
