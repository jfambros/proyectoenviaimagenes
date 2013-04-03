package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amber.proyecto.envia.imagenes.sw.Base64;

public class CodificaImagen {

	
	public String codificaImagenInternet(String ruta, String nombreImagen){
		Bitmap bitmapOrg = BitmapFactory.decodeFile(ruta+nombreImagen+".jpg");
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
		byte [] ba = bao.toByteArray();
		return Base64.encodeBytes(ba);
	}
	
	public DatosImagen divideBitmapArr(int chunkNumbers, String imagen)	{
		String partes[] = new String[chunkNumbers];
        int rows,cols;
        int i=0;

        int chunkHeight,chunkWidth;
  
        Bitmap bitmapOrg = BitmapFactory.decodeFile(imagen+".jpg");
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.getWidth(), bitmapOrg.getHeight(), true);
		Bitmap nuevoBM;
		ByteArrayOutputStream bao;
		byte [] ba;
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmapOrg.getHeight()/rows;
        chunkWidth = bitmapOrg.getWidth()/cols;
  
        int yCoord = 0;
        for(int x=0; x<rows; x++){
            int xCoord = 0;
            for(int y=0; y<cols; y++){
            	nuevoBM = Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight);
            	bao = new ByteArrayOutputStream();
            	nuevoBM.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            	ba = bao.toByteArray();
    			partes[i] = Base64.encodeBytes(ba);
    			//Log.i("Parte"+i, partes[i]);
    			i++; 
                xCoord += chunkWidth;
                nuevoBM = null;
            }
         
            yCoord += chunkHeight;
        }
        
        DatosImagen datosImagen = new DatosImagen();
        datosImagen.setWidth(bitmapOrg.getWidth());
        datosImagen.setHeigth(bitmapOrg.getHeight());
        datosImagen.setPartes(partes);
        
        bitmapOrg.recycle();
        scaledBitmap.recycle();
        bitmapOrg = null;
        scaledBitmap = null;
        System.gc();
        
        return datosImagen;
	}	

	
}
