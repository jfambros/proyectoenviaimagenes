package com.amber.proyecto.envia.imagenes.sw;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.amber.proyecto.envia.imagenes.sw.camara.ObtieneFoto;
import com.amber.proyecto.envia.imagenes.sw.utils.Cifrado;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistroUsuario extends Activity{
	private EditText etCorreoE;
	private EditText etNombre;
	private EditText etContra;
	private EditText etRepContra;
	private Button btnRegistrar;

	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrausuario);
		
		btnRegistrar = (Button)findViewById(R.id.btnNuevoUsuario);
		btnRegistrar.setOnClickListener(btnRegistrarPres);
		
		etCorreoE = (EditText)findViewById(R.id.etCorreoRegUsuario);
		etNombre = (EditText)findViewById(R.id.etNombreRegUsuario);
		etContra = (EditText)findViewById(R.id.etContraRegUsuario);
		etRepContra = (EditText)findViewById(R.id.etRepContraRegUsuario);
	}
	private OnClickListener btnRegistrarPres = new OnClickListener() {
		
		public void onClick(View arg0) {
			if (valida() == true){
				if (validateEmailAddress(etCorreoE.getText().toString()) != false){
					validaUsuario();
					mensaje("Mensaje", "¿Qué deseas realizar?");
				}
				else{
					Toast.makeText(RegistroUsuario.this, "Captura una dirección de correo válida", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	private boolean validateEmailAddress(String emailAddress){
	    String  expression="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";  
	       CharSequence inputStr = emailAddress;  
	       Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
	       Matcher matcher = pattern.matcher(inputStr);  
	       return matcher.matches();
	}
	private boolean valida(){
		if (!etContra.getText().toString().equals(etRepContra.getText().toString())){
			Toast.makeText(RegistroUsuario.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if (etCorreoE.getText().length() == 0 || etNombre.getText().length() == 0){
			Toast.makeText(RegistroUsuario.this, "Faltan datos", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	
	
	private void validaUsuario(){
		String URL = "http://"+Variables.HOST+"/pags/servicios.php";
		SoapObject request;
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#nuevoUsuario"; 
		String METHOD_NAME = "nuevoUsuario";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";

	
		try{
					request = new SoapObject(NAMESPACE, METHOD_NAME); 
					request.addProperty("correoUsuario", etCorreoE.getText().toString());
					request.addProperty("nombreUsuario", etNombre.getText().toString());
					request.addProperty("contraUsuario", Cifrado.getStringMessageDigest(etContra.getText().toString(),Cifrado.SHA256));
				    
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					
					envelope.dotNet = false;
					
					envelope.setOutputSoapObject(request);
					

					HttpTransportSE aht = new HttpTransportSE(URL); 

					aht.call(SOAP_ACTION, envelope);
					
					SoapObject result =  (SoapObject) envelope.bodyIn;
	                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
	                
					Log.i("resultado",spResul.toString());
					Toast.makeText(RegistroUsuario.this, "Registro", Toast.LENGTH_LONG).show();
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

	
	private void mensaje(String titulo, String msj){
        new AlertDialog.Builder(RegistroUsuario.this)
        .setTitle(titulo)
        .setMessage(msj)
        .setCancelable(false)
        .setPositiveButton("Ingresar contactos", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		Intent intent = new Intent();
        		intent.putExtra("correoUsuario", etCorreoE.getText().toString());
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		intent.setClass(RegistroUsuario.this, AgregarContacto.class);
        		startActivity(intent);
        		finish();
        	}
        })
        .setNeutralButton("Ir a inicio", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
        		Intent intent = new Intent();
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		intent.setClass(RegistroUsuario.this, Principal.class);
        		startActivity(intent);
        		finish();
			}
		})
		
        .show();   
	}
	
}


