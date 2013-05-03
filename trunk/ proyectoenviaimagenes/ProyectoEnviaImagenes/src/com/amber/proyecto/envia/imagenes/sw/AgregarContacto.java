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

import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class AgregarContacto extends Activity{
	private Bundle bundle;
	private EditText etNombreNvoContacto;
	private EditText etCorreoNvoContacto;
	private EditText etTelefonoNvoContacto;
	private Button btnNuevoContacto;
	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agregarcontacto);
		
		bundle = getIntent().getExtras();
		
		etNombreNvoContacto = (EditText)findViewById(R.id.etNombreNvoContacto);
		etCorreoNvoContacto = (EditText)findViewById(R.id.etCorreoNvoContacto);
		etTelefonoNvoContacto = (EditText)findViewById(R.id.etTelefonoNvoContacto);
		btnNuevoContacto = (Button)findViewById(R.id.btnNuevoContacto);
		btnNuevoContacto.setOnClickListener(btnNuevoContactoPres);
		
		
		
	}
	
	private OnClickListener btnNuevoContactoPres = new OnClickListener() {
		
		public void onClick(View v) {
			if (validateEmailAddress(etCorreoNvoContacto.getText().toString()) == true){
				agregarContacto();
				mensaje("Mensaje", "¿Qué deseas realizar?");
			}
			else{
				Toast.makeText(AgregarContacto.this, "Capture una dirección de correo electrónico válida", Toast.LENGTH_LONG).show();
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
	
	private void agregarContacto(){
		String URL = "http://"+Variables.HOST+"/pags/servicios.php";
		SoapObject request;
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#nuevoContacto"; 
		String METHOD_NAME = "nuevoContacto";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";

	
		try{
					request = new SoapObject(NAMESPACE, METHOD_NAME); 
					request.addProperty("nombreContacto", etNombreNvoContacto.getText().toString());
					request.addProperty("emailContacto", etCorreoNvoContacto.getText().toString());
					request.addProperty("telefonoContacto", etTelefonoNvoContacto.getText().toString());
					request.addProperty("correoUsuario", bundle.getString("correoUsuario"));
				    
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					
					envelope.dotNet = false;
					
					envelope.setOutputSoapObject(request);
					

					HttpTransportSE aht = new HttpTransportSE(URL); 

					aht.call(SOAP_ACTION, envelope);
					
					SoapObject result =  (SoapObject) envelope.bodyIn;
	                SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
	                
					Log.i("resultado",spResul.toString());
					Toast.makeText(AgregarContacto.this, "Contacto registrado", Toast.LENGTH_LONG).show();
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
        new AlertDialog.Builder(AgregarContacto.this)
        .setTitle(titulo)
        .setMessage(msj)
        .setCancelable(false)
        .setPositiveButton("Agregar otro contacto", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		etCorreoNvoContacto.setText("");
        		etNombreNvoContacto.setText("");
        		etTelefonoNvoContacto.setText("");
        		setResult(RESULT_OK);
        	}
        })
        .setNeutralButton("Ir a inicio", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
        		Intent intent = new Intent();
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		intent.setClass(AgregarContacto.this, Principal.class);
        		startActivity(intent);
        		finish();
			}
		})
		
        .show();   
	}
}
