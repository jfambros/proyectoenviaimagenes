package com.amber.proyecto.envia.imagenes.sw;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.mibd.BD;
import com.amber.proyecto.envia.imagenes.sw.utils.Categoria;
import com.amber.proyecto.envia.imagenes.sw.utils.Imagen;
import com.amber.proyecto.envia.imagenes.sw.utils.ImagenParcelable;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class Busca extends Activity{
	private ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	private ListView lista;
	private ArrayList<String> opciones = new ArrayList<String>();
	private ImageView ivBusca;
	private ImageView ivAtrasBusca;
    private String URL = "http://"+Variables.HOST+"/pags/servicios.php";
    private String queryFinal;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busca);
		ivBusca = (ImageView)findViewById(R.id.ivBusca);
		ivBusca.setOnClickListener(ivBuscaPres);
		llenaCategorias();
		lista = (ListView)findViewById(R.id.lvOpcionesBusca);
		lista.setOnItemClickListener(listaPres);
		
		ivAtrasBusca =(ImageView)findViewById(R.id.ivAtrasBusca1);
		ivAtrasBusca.setOnClickListener(ivAtrasBuscaPres);
	}
	
	
	private void llenaCategorias(){
		BD bd = new BD(this);
		categorias = bd.obtieneCategorias();
		ArrayList<String> nombreCat = new ArrayList<String>();
		for(int i=0; i<categorias.size(); i++){
			nombreCat.add(categorias.get(i).getNombreCategoria());
		}
		
		ArrayAdapter<String> contenidoLista = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_multiple_choice,
				nombreCat);
		
		ListView listaCat = (ListView)findViewById(R.id.lvOpcionesBusca);
		listaCat.setAdapter(contenidoLista);
	}
	

	private OnClickListener ivAtrasBuscaPres = new OnClickListener() {
		
		public void onClick(View v) {
			Intent intent = new Intent();
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(Busca.this, Principal.class);
			startActivity(intent);
			finish();
		}
	};
	
	
	private OnItemClickListener listaPres = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int posi,	long arg3) {
			String opcion = Integer.toString(categorias.get(posi).getIdCategoria());
			Log.i("número ",":"+posi);
			if (!((CheckedTextView)arg1).isChecked() && !opciones.contains(opcion)){
				//Log.i("Seleccionado ", categorias.get(posi).getNombreCategoria());			
				opciones.add(opcion);
			}
			if (opciones.contains(opcion) && ((CheckedTextView)arg1).isChecked() ){
				//Log.i("Borrado: ", categorias.get(posi).getNombreCategoria());
				opciones.remove(opcion);
			}

		}
	};
	
	private OnClickListener ivBuscaPres = new OnClickListener() {
		
		public void onClick(View v) {
			if (opciones.size() == 0){
				Toast.makeText(Busca.this, "Selecciona al menos una opción", Toast.LENGTH_LONG).show();
			}else{
				ArrayList<ImagenParcelable> resultados = new ArrayList<ImagenParcelable>();
				resultados = obtieneImagenesSW();
				if (resultados.size() != 0){
					Intent intent = new Intent();
					intent.putParcelableArrayListExtra("imagenes", resultados);
					intent.putExtra("queryFinal", queryFinal);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(Busca.this, Mapa.class);
					startActivity(intent);
					finish();
				}
				else{
					Toast.makeText(Busca.this, "No hay registros", Toast.LENGTH_LONG).show();
				}
				
				/*
				ArrayList<ImagenParcelable> imagenes = new ArrayList<ImagenParcelable>();
				BD bd = new BD(Busca.this);
				imagenes = bd.buscaLugares(opciones);
				bd.close();
				Intent intent = new Intent();
				intent.putParcelableArrayListExtra("imagenes", imagenes);
	    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(Busca.this, Mapa.class);
				startActivity(intent);
				finish();
				*/
			}
		}
	};
	
	private String llenaQuery(ArrayList<String> lugares){
		StringBuffer query = new StringBuffer("SELECT i.*, c.nombreCategoria from imagen i, categoria c where (i.idCategoria = ");
		if (lugares.size() == 1){
			query.append(lugares.get(0));
		}else{
			for (int i=1;i<=lugares.size(); i++ ){
				query.append(lugares.get(i-1));
				if (i<lugares.size()){
					query.append(" or i.idCategoria =  ");
				}
			}
		}
		query.append(") and i.idCategoria = c.idCategoria");
		Log.i("Query", query.toString());
		return query.toString();
	}
	
	private ArrayList<ImagenParcelable> obtieneImagenesSW(){
		String SOAP_ACTION="capeconnect:servicios:serviciosPortType#obtieneImagenes"; 
		String METHOD_NAME = "obtieneImagenes";
		String NAMESPACE = "http://www.your-company.com/servicios.wsdl";
		
		SoapSerializationEnvelope envelope;
        HttpTransportSE httpt;
        ArrayList<ImagenParcelable> imagenParcelable = new ArrayList<ImagenParcelable>();
        try{
        	queryFinal = llenaQuery(opciones);
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("opciones", queryFinal);
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
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
