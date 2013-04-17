package com.amber.proyecto.envia.imagenes.sw;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amber.proyecto.envia.imagenes.sw.mibd.BD;
import com.amber.proyecto.envia.imagenes.sw.utils.Categoria;

public class Busca extends Activity{
	private ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	private ListView lista;
	private ArrayList<String> opciones = new ArrayList<String>();
	private ImageView ivBusca;

	
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
	

	private OnItemClickListener listaPres = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int posi,	long arg3) {
			if (!((CheckedTextView)arg1).isChecked() && !opciones.contains(categorias.get(posi).getNombreCategoria())){
				Log.i("Seleccionado", categorias.get(posi).getNombreCategoria());			
				opciones.add(categorias.get(posi).getNombreCategoria());
			}
			if (opciones.contains(categorias.get(posi).getNombreCategoria()) && ((CheckedTextView)arg1).isChecked() ){
				Log.i("Borrado:", categorias.get(posi).getNombreCategoria());
				opciones.remove(categorias.get(posi).getNombreCategoria());
			}

		}
	};
	
	private OnClickListener ivBuscaPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			for (int i=0; i<opciones.size(); i++){
				Log.i("opción "+i, opciones.get(i));
			}
			
		}
	};


}
