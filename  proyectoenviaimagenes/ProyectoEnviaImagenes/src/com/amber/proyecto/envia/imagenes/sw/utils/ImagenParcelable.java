package com.amber.proyecto.envia.imagenes.sw.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ImagenParcelable implements Parcelable{
	private String nombreImagen;
	private double latitud;
	private double longitud;
	private int idCategoria;
	private String comentario;
	private String nombreCategoria;
	private float calificacion;

	public float getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(float calificacion) {
		this.calificacion = calificacion;
	}

	public String getNombreCategoria() {
		return nombreCategoria;
	}

	public void setNombreCategoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}

	public String getNombreImagen() {
		return nombreImagen;
	}

	public void setNombreImagen(String nombreImagen) {
		this.nombreImagen = nombreImagen;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public static final Parcelable.Creator<ImagenParcelable> CREATOR  = new Creator<ImagenParcelable>() {
		
		@Override
		public ImagenParcelable[] newArray(int size) {
			return new ImagenParcelable[size];
		}
		
		@Override
		public ImagenParcelable createFromParcel(Parcel in) {
			return new ImagenParcelable(in);
		}
	};
	
	
	public ImagenParcelable(){
		
	}
	
	public ImagenParcelable(Parcel in){
		readFromParcel(in);
		
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(nombreImagen);
		dest.writeDouble(latitud);
		dest.writeDouble(longitud);
		dest.writeInt(idCategoria);
		dest.writeString(comentario);
		dest.writeString(nombreCategoria);
		
	}
	
	private void readFromParcel(Parcel in){
		nombreImagen = in.readString();
		latitud = in.readDouble();
		longitud = in.readDouble();
		idCategoria = in.readInt();
		comentario = in.readString();
		nombreCategoria = in.readString();
	}

}
