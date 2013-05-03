package com.amber.proyecto.envia.imagenes.sw;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.amber.proyecto.envia.imagenes.sw.utils.Cifrado;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InicioSesion extends Activity{
	private EditText etCorreoE;
	private EditText etContra;
	private Button btnInicioSesion;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iniciosesion);
		etCorreoE = (EditText)findViewById(R.id.etCorreoEInicioSesion);
		etContra = (EditText)findViewById(R.id.etContraInicioSesion);
		btnInicioSesion = (Button)findViewById(R.id.btnIniciarSesion);
		btnInicioSesion.setOnClickListener(btnInicioSesionPres);
	}
	
	private OnClickListener btnInicioSesionPres = new OnClickListener() {
		
		public void onClick(View v) {
			
		}
	};
	
	
	private void validaUsuario(){
		String URL = "http://"+Variables.HOST+"/pags/servicios.php";
		SoapObject request;
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#validaUsuario"; 
		String METHOD_NAME = "validaUsuario";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";

	
		try{
					request = new SoapObject(NAMESPACE, METHOD_NAME); 
					request.addProperty("correoUsuario", etCorreoE.getText().toString());
					request.addProperty("contraUsuario", Cifrado.getStringMessageDigest(etContra.getText().toString(),Cifrado.SHA256));
				    
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					
					envelope.dotNet = false;
					
					envelope.setOutputSoapObject(request);
					

					HttpTransportSE aht = new HttpTransportSE(URL); 

					aht.call(SOAP_ACTION, envelope);
					
					SoapObject result =  (SoapObject) envelope.bodyIn;
	                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
	                
					Log.i("resultado",spResul.toString());
					Toast.makeText(InicioSesion.this, "Registro", Toast.LENGTH_LONG).show();
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
}
