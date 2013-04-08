package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	
	
	public ContenidoArray codificaImagenBytes(int chunkNumbers, String nombreImagen, int CHUNK_SIZE){
		
		File willBeRead = new File (nombreImagen+".jpg");
		int FILE_SIZE = (int) willBeRead.length();
		//ArrayList<String> nameList = new ArrayList<String> ();
		int NUMBER_OF_CHUNKS = 0;
		byte[] temporary = null;
		ContenidoArray contenidoArray = new ContenidoArray();

		/*
		WeakReference<Bitmap> bitmapOrg = new WeakReference<Bitmap>(BitmapFactory.decodeFile(nombreImagen+".jpg"));
		
        datosImagen.setWidth(bitmapOrg.get().getWidth());
        datosImagen.setHeigth(bitmapOrg.get().getHeight());
        bitmapOrg.get().recycle();
		*/
		
		try {
			   InputStream inStream = null;
			   int totalBytesRead = 0;
			   
			   try {
			    inStream = new BufferedInputStream ( new FileInputStream( willBeRead ));
			    
			    while ( totalBytesRead < FILE_SIZE ){
			     String PART_NAME ="data"+NUMBER_OF_CHUNKS+".bin";
			     int bytesRemaining = FILE_SIZE-totalBytesRead;
			     if ( bytesRemaining < CHUNK_SIZE ) // Remaining Data Part is Smaller Than CHUNK_SIZE
			                // CHUNK_SIZE is assigned to remain volume
			     {
			      CHUNK_SIZE = bytesRemaining;
			      //System.out.println("CHUNK_SIZE: "+CHUNK_SIZE);
			     }
			     temporary = new byte[CHUNK_SIZE]; //Temporary Byte Array
			     int bytesRead = inStream.read(temporary, 0, CHUNK_SIZE);
			     
			     contenidoArray.add(Base64.encodeBytes(temporary));
			     //Log.i("Parte ",Base64.encodeBytes(temporary));
			     if ( bytesRead > 0) // If bytes read is not empty
			     {
			      totalBytesRead += bytesRead;
			      NUMBER_OF_CHUNKS++;
			     }
			     
			     //write(temporary, "D://"+PART_NAME);
			     //nameList.add("D://"+PART_NAME);
			     //System.out.println("Total Bytes Read: "+totalBytesRead);
			    }
			    
			   }
			   finally {
			    inStream.close();
			   }
		

		
		}
		catch (FileNotFoundException ex){
		 ex.printStackTrace();
		}
		catch (IOException ex){
		 ex.printStackTrace();
		}
		return contenidoArray;
	}

	
}
