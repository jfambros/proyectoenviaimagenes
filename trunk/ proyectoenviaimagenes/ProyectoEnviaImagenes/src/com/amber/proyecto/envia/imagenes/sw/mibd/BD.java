package com.amber.proyecto.envia.imagenes.sw.mibd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BD extends SQLiteOpenHelper{
	private static final String nombreBD = "enviaimagen.db";
	private static final String nombreTablaCategorias = "categorias";
	private static final String tablaImagenes = "create table imagenes(" +
			"nombreImagen text not null, " +
			"contenidoImagen text not null, " +
			"longitud real not null," +
			"latitud real not null," +
			"idCategoria integer not null," +
			"descripcion text," +
			"constraint nombrePK primary key(nombreImagen) );";
	private static final String tablaCategorias = "create table "+nombreTablaCategorias+ "(" +
			"idCategoria integer not null," +
			"nombreCategoria text not null," +
			"constraint idCategoriaPK primary key(idCategoria) );";

	public BD(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public BD(Context context){
		super(context, nombreBD, null,33);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(tablaCategorias);
		db.execSQL(tablaImagenes);
		insertaCategorias(db);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	private void insertaCategorias(SQLiteDatabase db){
		ContentValues cv = new ContentValues();
		cv.put("idCategoria", 1);
		cv.put("nombreCategoria", "Hotel");
		db.insert(nombreTablaCategorias, null, cv);
		cv.put("idCategoria", 2);
		cv.put("nombreCategoria", "Restaurante");
		db.insert(nombreTablaCategorias, null, cv);	
		cv.put("idCategoria", 3);
		cv.put("nombreCategoria", "Mercado");
		db.insert(nombreTablaCategorias, null, cv);
		cv.put("idCategoria", 4);
		cv.put("nombreCategoria", "Sitio arqueológico");
		db.insert(nombreTablaCategorias, null, cv);
		cv.put("idCategoria", 5);
		cv.put("nombreCategoria", "Otro");
		db.insert(nombreTablaCategorias, null, cv);		
	
	}
	
	public Cursor obtieneCategorias(){
		 SQLiteDatabase db=this.getReadableDatabase();
		 Cursor cur=db.rawQuery("SELECT * from "+ nombreTablaCategorias,new String [] {});	 
		 return cur;
	}
	

}
