package com.amber.proyecto.envia.imagenes.sw.mibd;

import java.util.ArrayList;

import com.amber.proyecto.envia.imagenes.sw.utils.Categoria;
import com.amber.proyecto.envia.imagenes.sw.utils.Imagen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BD extends SQLiteOpenHelper{
	private static final String nombreBD = "enviaimagen.db";
	private static final String nombreTablaCategorias = "categorias";
	private static final String nombreTablaImagenes = "imagenes";
	private static final String nombreTablaContenido = "contenido";
	private static final String tablaImagenes = "create table "+ nombreTablaImagenes+"(" +
			"nombreImagen text not null, " +
			"latitud real not null," +
			"longitud real not null," +
			"idCategoria integer not null," +
			"comentario text," +
			"constraint nombrePK primary key(nombreImagen) );";
	private static final String tablaContenido = "create table "+nombreTablaContenido+"("+
			"nombreImagen text not null, "+
			"parte1 text not null, "+
			"parte2 text not null, "+
			"parte3 text not null, "+
			"parte4 text not null, "+
			"parte5 text not null, "+
			"parte6 text not null, "+
			"parte7 text not null, "+
			"parte8 text not null, "+
			"parte9 text not null, "+			
			"constraint nombreImagenPK primary key(nombreImagen)," +
			"constraint nombreImagenFK foreign key(nombreImagen) " +
			"references "+nombreTablaImagenes+"(nombreImagen) );";
			
	private static final String tablaCategorias = "create table "+nombreTablaCategorias+ "(" +
			"idCategoria integer not null," +
			"nombreCategoria text not null," +
			"constraint idCategoriaPK primary key(idCategoria) );";

	public BD(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public BD(Context context){
		super(context, nombreBD, null,1);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(tablaCategorias);
		db.execSQL(tablaImagenes);
		db.execSQL(tablaContenido);
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
		cv.put("nombreCategoria", "Museo");
		db.insert(nombreTablaCategorias, null, cv);
		cv.put("idCategoria", 6);
		cv.put("nombreCategoria", "Diversión");
		db.insert(nombreTablaCategorias, null, cv);		
		cv.put("idCategoria", 7);
		cv.put("nombreCategoria", "Otro");
		db.insert(nombreTablaCategorias, null, cv);				
	
	}
	
	public void insertaCategoria(int idCategoria, String nombreCategoria){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("idCategoria", idCategoria);
		cv.put("nombreCategoria", nombreCategoria);
		db.insert(nombreTablaCategorias, null, cv);	
		db.close();
	}
	
	public ArrayList<Categoria> obtieneCategorias(){
		 SQLiteDatabase db=this.getReadableDatabase();
		 ArrayList<Categoria> categorias = new ArrayList<Categoria>();
		 Cursor cursor=db.rawQuery("SELECT * from "+ nombreTablaCategorias, null);	 
		 if (cursor.moveToFirst()) {
	        do {
	        	Categoria cat = new Categoria();
	        	cat.setIdCategoria(Integer.parseInt(cursor.getString(0)));
	        	cat.setNombreCategoria(cursor.getString(1));
			   categorias.add(cat);
		   } while (cursor.moveToNext());
		}
			  cursor.close();
			  db.close();
		 return categorias;
	}
	
	public ArrayList<Imagen> obtieneImagenes(){
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Imagen> imagenes = new ArrayList<Imagen>();
		Cursor cursor = db.rawQuery("SELECT * from "+nombreTablaImagenes, null);
		if (cursor.moveToFirst()) {
	        do {
	        	Imagen ima = new Imagen();
	        	ima.setNombreImagen(cursor.getString(0));
	        	ima.setLatitud(cursor.getDouble(1));
	        	ima.setLongitud(cursor.getDouble(2));
	        	ima.setIdCategoria(cursor.getInt(3));
	        	ima.setComentario(cursor.getString(4));
			    imagenes.add(ima);
		   } while (cursor.moveToNext());
		}
			  cursor.close();
			  db.close();
		 return imagenes;
	}
	
	public Imagen obtieneImagenBorra(){
		SQLiteDatabase db = this.getReadableDatabase();
    	Imagen ima = new Imagen();
		Cursor cursor = db.rawQuery("SELECT * from "+nombreTablaImagenes+" limit 1", null);
		if (cursor.moveToFirst()) {
	        do {

	        	ima.setNombreImagen(cursor.getString(0));
	        	ima.setLatitud(cursor.getDouble(1));
	        	ima.setLongitud(cursor.getDouble(2));
	        	ima.setIdCategoria(cursor.getInt(3));
	        	ima.setComentario(cursor.getString(4));
			    borraImagen(cursor.getString(0));
			    borraContenido(cursor.getString(0));
		   } while (cursor.moveToNext());
		}
		
			  cursor.close();
			  db.close();
		 return ima;
	}
	
	
	
	
	public int cuentaRegImagenes(){
		int total;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * from "+nombreTablaImagenes, null);
		total = cursor.getCount();
		cursor.close();
		db.close();
		return total;
	}
	
	public void insertaContenido(String nombreImagen, String[] contenido){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("nombreImagen", nombreImagen);
		cv.put("parte1", contenido[0]);
		cv.put("parte2", contenido[1]);
		cv.put("parte3", contenido[2]);
		cv.put("parte4", contenido[3]);
		cv.put("parte5", contenido[4]);
		cv.put("parte6", contenido[5]);
		cv.put("parte7", contenido[6]);
		cv.put("parte8", contenido[7]);
		cv.put("parte9", contenido[8]);		
		db.insert(nombreTablaContenido, null, cv);
		db.close();
	}
	public void insertaImagen(String nombreImagen, double latitud, double longitud, int idCategoria, String comentario){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("nombreImagen", nombreImagen);
		cv.put("latitud", latitud);
		cv.put("longitud", longitud);
		cv.put("idCategoria", idCategoria);
		cv.put("comentario", comentario);
		db.insert(nombreTablaImagenes, null, cv);
		db.close();
	}
	
	public void borraCategorias(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+nombreTablaCategorias);
		db.close();
		
	}
	
	public void borraImagen(String nombImagen){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+nombreTablaImagenes+" where nombreImagen = '"+nombImagen+"';");
		db.close();
			
	}
	
	public void borraContenido(String nombreImagen){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+nombreTablaContenido+" where nombreImagen = '"+nombreImagen+"';");
		db.close();		
	}

	public void borraImagenes(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+nombreTablaImagenes);
		db.close();
	}
	

}