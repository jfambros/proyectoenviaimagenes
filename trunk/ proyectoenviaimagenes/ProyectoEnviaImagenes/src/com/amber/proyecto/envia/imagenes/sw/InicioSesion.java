package com.amber.proyecto.envia.imagenes.sw;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.usuario.PrincipalUsuario;
import com.amber.proyecto.envia.imagenes.sw.utils.Cifrado;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class InicioSesion extends Activity{
	private EditText etCorreoE;
	private EditText etContra;
	private Button btnInicioSesion;
	private String correoUsuario;
	private String nombreUsuario;
	private ImageView ivAtrasInicioSesion;
	private Bundle bundle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iniciosesion);
		etCorreoE = (EditText)findViewById(R.id.etCorreoEInicioSesion);
		etContra = (EditText)findViewById(R.id.etContraInicioSesion);
		btnInicioSesion = (Button)findViewById(R.id.btnIniciarSesion);
		btnInicioSesion.setOnClickListener(btnInicioSesionPres);
		
		ivAtrasInicioSesion = (ImageView)findViewById(R.id.ivAtrasInicioSesion);
		ivAtrasInicioSesion.setOnClickListener(ivAtrasInicioSPres);
		
		bundle = getIntent().getExtras();
	}
	
	private OnClickListener ivAtrasInicioSPres = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(InicioSesion.this, Principal.class);
			startActivity(intent);
			finish();
		}
	};
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private OnClickListener btnInicioSesionPres = new OnClickListener() {
		
		public void onClick(View v) {
			if (etCorreoE.getText().length() != 0 && etContra.getText().length() !=0){
				if (validaUsuario() == true){
					Toast.makeText(InicioSesion.this, "Iniciando sesión...", Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
		    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    		intent.putExtra("nombreUsuario", nombreUsuario);
		    		intent.putExtra("correoUsuario", correoUsuario);
		    		if (bundle.getInt("origen") == Variables.ENVIAIMAGEN){
		    			intent.setClass(InicioSesion.this, PrincipalUsuario.class);
		    		}
		    		else{
		    			intent.setClass(InicioSesion.this, PrincipalUsuario.class);
		    		}
		    		startActivity(intent);
		    		finish();					
				}
				else{
					Toast.makeText(InicioSesion.this, "Correo o contraseña incorrecto", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(InicioSesion.this, "Captura los datos", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	private boolean validaUsuario(){
		String URL = "http://"+Variables.HOST+"/pags/servicios.php";
		SoapObject request;
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#validaUsuario"; 
		String METHOD_NAME = "validaUsuario";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";

	
		try{
					request = new SoapObject(NAMESPACE, METHOD_NAME); 
					request.addProperty("correoElectronico", etCorreoE.getText().toString());
					request.addProperty("contrasenia", Cifrado.getStringMessageDigest(etContra.getText().toString(),Cifrado.SHA256));
				    
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					
					envelope.dotNet = false;
					
					envelope.setOutputSoapObject(request);
					

					HttpTransportSE aht = new HttpTransportSE(URL); 

					aht.call(SOAP_ACTION, envelope);
					
					SoapObject result =  (SoapObject) envelope.getResponse();
	                correoUsuario = ((SoapPrimitive)result.getProperty(0)).toString();
	                nombreUsuario = ((SoapPrimitive)result.getProperty(1)).toString();
	                
	                if (correoUsuario.equals("null")){
	                	return false;
	                }else{
	                	return true;
	                }
	                

		    } 
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}
}
