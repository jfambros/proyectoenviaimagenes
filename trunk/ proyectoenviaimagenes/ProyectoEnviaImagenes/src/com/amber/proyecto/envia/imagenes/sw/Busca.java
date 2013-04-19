package com.amber.proyecto.envia.imagenes.sw;

import java.util.ArrayList;

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

public class Busca extends Activity{
	private ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	private ListView lista;
	private ArrayList<String> opciones = new ArrayList<String>();
	private ImageView ivBusca;
	private ImageView ivAtrasBusca;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int posi,	long arg3) {
			String opcion = Integer.toString(categorias.get(posi).getIdCategoria());
			Log.i("número ",":"+posi);
			if (!((CheckedTextView)arg1).isChecked() && !opciones.contains(opcion)){
				Log.i("Seleccionado ", categorias.get(posi).getNombreCategoria());			
				opciones.add(opcion);
			}
			if (opciones.contains(opcion) && ((CheckedTextView)arg1).isChecked() ){
				Log.i("Borrado: ", categorias.get(posi).getNombreCategoria());
				opciones.remove(opcion);
			}

		}
	};
	
	private OnClickListener ivBuscaPres = new OnClickListener() {
		
		public void onClick(View v) {
			if (opciones.size() == 0){
				Toast.makeText(Busca.this, "Selecciona al menos una opción", Toast.LENGTH_LONG).show();
			}else{
				BD bd = new BD(Busca.this);
				bd.buscaLugares(opciones);
				bd.close();
				Intent intent = new Intent();
	    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(Busca.this, Mapa.class);
				startActivity(intent);
				finish();
			}
		}
	};
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
