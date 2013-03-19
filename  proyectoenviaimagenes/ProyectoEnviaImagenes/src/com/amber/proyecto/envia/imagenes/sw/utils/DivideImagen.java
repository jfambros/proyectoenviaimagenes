package com.amber.proyecto.envia.imagenes.sw.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class DivideImagen {
	private String imagen;
	public DivideImagen(String imagen){
		this.imagen = imagen;
	}
	
	public Bitmap[] divideBitmap()	{
		Bitmap bitmapOrg = BitmapFactory.decodeFile(imagen+".jpg");
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, 240, 240, true);
		Bitmap[] imgs = new Bitmap[9];
		imgs[0] = Bitmap.createBitmap(scaledBitmap, 0, 0, 80 , 80);
		imgs[1] = Bitmap.createBitmap(scaledBitmap, 80, 0, 80, 80);
		imgs[2] = Bitmap.createBitmap(scaledBitmap,160, 0, 80,80);
		imgs[3] = Bitmap.createBitmap(scaledBitmap, 0, 80, 80, 80);
		imgs[4] = Bitmap.createBitmap(scaledBitmap, 80, 80, 80,80);
		imgs[5] = Bitmap.createBitmap(scaledBitmap, 160, 80,80,80);
		imgs[6] = Bitmap.createBitmap(scaledBitmap, 0, 160, 80,80);
		imgs[7] = Bitmap.createBitmap(scaledBitmap, 80, 160,80,80);
		imgs[8] = Bitmap.createBitmap(scaledBitmap, 160,160,80,80);
		return imgs;
	}
	

}
