package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amber.proyecto.envia.imagenes.sw.Base64;

public class CodificaImagen {

	
	public String codificaImagenInternet(String ruta, String nombreImagen){
		Bitmap bitmapOrg = BitmapFactory.decodeFile(ruta+nombreImagen+".jpg");
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
		byte [] ba = bao.toByteArray();
		return Base64.encodeBytes(ba);
	}
}
