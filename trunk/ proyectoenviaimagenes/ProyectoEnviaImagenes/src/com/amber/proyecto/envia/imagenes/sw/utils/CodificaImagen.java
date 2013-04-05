package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

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
	
	public DatosImagen divideBitmapArr(int chunkNumbers, String imagen) throws IOException	{
        int rows,cols;
        ContenidoArray contenidoArray = new ContenidoArray();

        int chunkHeight,chunkWidth;
  
        Bitmap bitmapOrg = BitmapFactory.decodeFile(imagen+".jpg");
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.getWidth(), bitmapOrg.getHeight(), true);
		WeakReference<Bitmap> nuevoBM ;
		ByteArrayOutputStream bao;
		byte [] ba;
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmapOrg.getHeight()/rows;
        chunkWidth = bitmapOrg.getWidth()/cols;
        
        DatosImagen datosImagen = new DatosImagen();
        datosImagen.setWidth(bitmapOrg.getWidth());
        datosImagen.setHeigth(bitmapOrg.getHeight());
        bitmapOrg.recycle();
  
        int yCoord = 0;
        for(int x=0; x<rows; x++){
            int xCoord = 0;
            for(int y=0; y<cols; y++){
            	nuevoBM = new WeakReference<Bitmap>(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
            	bao = new ByteArrayOutputStream();
            	nuevoBM.get().compress(Bitmap.CompressFormat.JPEG, 100, bao);
            	ba = bao.toByteArray();
    			contenidoArray.add(Base64.encodeBytes(ba));
                xCoord += chunkWidth;
                //Log.i("contenido", Base64.encodeBytes(ba));
                bao.flush();
                bao.close();
                //nuevoBM.recycle();
                System.gc();
            }
         
            yCoord += chunkHeight;
        }
        
        
        datosImagen.setPartes(contenidoArray);
        
        //contenidoArray.clear();
        bitmapOrg.recycle();
        scaledBitmap.recycle();
        bitmapOrg = null;
        scaledBitmap = null;

        nuevoBM = null;
        System.gc();
        
        return datosImagen;
	}	

	
}
