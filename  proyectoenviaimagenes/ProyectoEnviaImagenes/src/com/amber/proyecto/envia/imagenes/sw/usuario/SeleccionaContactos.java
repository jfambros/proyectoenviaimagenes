package com.amber.proyecto.envia.imagenes.sw.usuario;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.Principal;
import com.amber.proyecto.envia.imagenes.sw.R;
import com.amber.proyecto.envia.imagenes.sw.utils.Contacto;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class SeleccionaContactos extends Activity{
	private Bundle bundle;
	private ArrayList<Contacto> contactos = new ArrayList<Contacto>();
	private ListView listaCont;
	private ArrayList<String> correos = new ArrayList<String>();
	private ImageView ivEnviaCorreo;
	private ImageView ivAtrasSelCont;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seleccionacontactos);
		
		bundle = getIntent().getExtras();
		llenaLista();
		listaCont = (ListView)findViewById(R.id.lvOpcionesSeleccionaCont);
		listaCont.setOnItemClickListener(listaPres);
		
		ivEnviaCorreo = (ImageView)findViewById(R.id.ivEnviaCoSeleccionaCont);
		ivEnviaCorreo.setOnClickListener(ivEnviaCorreoPres);
		
		ivAtrasSelCont = (ImageView)findViewById(R.id.ivAtrasSeleccionaCont);
		ivAtrasSelCont.setOnClickListener(ivAtrasSelContPres);
		
		
	}
	
	private ArrayList<Contacto> obtieneContactos(){
	    String URL = "http://"+Variables.HOST+"/pags/servicios.php";
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#obtieneListaContacto"; 
		String METHOD_NAME = "obtieneListaContacto";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		ArrayList<Contacto> contactos = new ArrayList<Contacto>();
		
		SoapSerializationEnvelope envelope;
        HttpTransportSE httpt;
        
        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
               
	        httpt = new HttpTransportSE(URL);
	        envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11 );
	        envelope.dotNet = false;
	        request.addProperty("correoUsuario", bundle.getString("correoUsuario"));
	        envelope.setOutputSoapObject(request);
	        httpt.call(SOAP_ACTION, envelope);
	        SoapObject result2 =  (SoapObject) envelope.getResponse();
	        
	        for(int cont=0; cont< result2.getPropertyCount(); cont ++){
	        	SoapObject resultados = (SoapObject) result2.getProperty(cont);
	        	//primitivas
	        	SoapPrimitive idContacto = (SoapPrimitive) resultados.getProperty("idNuevoContacto");
	        	SoapPrimitive nombreContacto = (SoapPrimitive) resultados.getProperty("nombreContacto");
	        	SoapPrimitive emailContacto = (SoapPrimitive) resultados.getProperty("emailContacto");
	        	SoapPrimitive telefonoContacto = (SoapPrimitive) resultados.getProperty("telefonoContacto");
	        	SoapPrimitive correoUsuario = (SoapPrimitive) resultados.getProperty("correoUsuario");
	        	
	        	Contacto c = new Contacto();
	        	c.setIdContacto(Integer.parseInt(idContacto.toString()));
	        	c.setNombreContacto(nombreContacto.toString());
	        	c.setEmailContacto(emailContacto.toString());
	        	c.setTelefonoContacto(telefonoContacto.toString());
	        	c.setCorreoUsuario(correoUsuario.toString());
	        	
	        	contactos.add(c);
	        	

	        }

            
        }
        	catch(Exception err){
        		Log.e("Error en llena contactos", err.toString());
        	}
        return contactos;
	}
	
	private void enviaCorreo(String correoContacto){
	    String URL = "http://"+Variables.HOST+"/pags/servicios.php";
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#enviaCorreo"; 
		String METHOD_NAME = "enviaCorreo";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		
		SoapSerializationEnvelope envelope;
        HttpTransportSE httpt;
        
        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
               
	        httpt = new HttpTransportSE(URL);
	        envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11 );
	        envelope.dotNet = false;
	        request.addProperty("nombreImagen", bundle.getString("nombreImagen"));
	        request.addProperty("correoContacto", correoContacto);
	        request.addProperty("correoUsuario", bundle.getString("correoUsuario"));
	        request.addProperty("host", Variables.HOST);
	        
	        envelope.setOutputSoapObject(request);
	        httpt.call(SOAP_ACTION, envelope);
	        SoapObject result =  (SoapObject) envelope.bodyIn;
            SoapPrimitive spResul = (SoapPrimitive) result.getProperty("result");
            Log.i("correo", spResul.toString());
	        //SoapObject result2 =  (SoapObject) envelope.getResponse();
	        
	        
	        

            
        }
        	catch(Exception err){
        		Log.e("Error en enviar correo", err.toString());
        	}	
	}
	
	private void llenaLista(){
		ArrayList<String> correoNombre = new ArrayList<String>();
		contactos = obtieneContactos();
		for (int i=0; i<contactos.size(); i++){
			correoNombre.add("Correo: "+contactos.get(i).getEmailContacto()+"  Nombre:"+contactos.get(i).getNombreContacto());
		}
		
		ArrayAdapter<String> contenidoLista = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_multiple_choice,correoNombre);
		
		ListView listaCat = (ListView)findViewById(R.id.lvOpcionesSeleccionaCont);
		listaCat.setAdapter(contenidoLista);
		
	}
	
	private OnItemClickListener listaPres = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int posi,	long arg3) {
//			Log.i("Seleccionado", contactos.get(posi).getEmailContacto());
			if (!((CheckedTextView)arg1).isChecked() && !correos.contains(contactos.get(posi).getEmailContacto())){		
				correos.add(contactos.get(posi).getEmailContacto());
			}
			if (correos.contains(contactos.get(posi).getEmailContacto()) && ((CheckedTextView)arg1).isChecked() ){
				correos.remove(contactos.get(posi).getEmailContacto());
			}
		}
	};
	
	private android.view.View.OnClickListener ivEnviaCorreoPres = new android.view.View.OnClickListener() {

		public void onClick(View v) {
			if (correos.size() == 0){
				Toast.makeText(SeleccionaContactos.this, "Selecciona al menos un contacto", Toast.LENGTH_LONG).show();
			}else{
				for (int i=0; i<correos.size(); i++){
					enviaCorreo(correos.get(i));
				}
				Toast.makeText(SeleccionaContactos.this, "Correos enviados", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private android.view.View.OnClickListener ivAtrasSelContPres = new android.view.View.OnClickListener(){

		public void onClick(View v) {
			Intent intent = new Intent();
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(SeleccionaContactos.this, Principal.class);
			startActivity(intent);
			finish();			
		}
		
	};

}
