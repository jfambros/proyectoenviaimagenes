package com.amber.proyecto.envia.imagenes.sw.utils;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;


public class DivideImagen {
	private String imagen;
	public DivideImagen(String imagen){
		this.imagen = imagen;
	}
	
	public ArrayList<Bitmap> divideBitmap(int chunkNumbers)	{
		
	     //For the number of rows and columns of the grid to be displayed
        int rows,cols;
  
        //For height and width of the small image chunks 
        int chunkHeight,chunkWidth;
  
        //To store all the small image chunks in bitmap format in this list 
        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);
  
        //Getting the scaled bitmap of the source image
  
        Bitmap bitmapOrg = BitmapFactory.decodeFile(imagen+".jpg");
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, bitmapOrg.getWidth(), bitmapOrg.getHeight(), true);
        
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmapOrg.getHeight()/rows;
        chunkWidth = bitmapOrg.getWidth()/cols;
  
        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int x=0; x<rows; x++){
            int xCoord = 0;
            for(int y=0; y<cols; y++){
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return chunkedImages;
	}	
		/*
		int rows,cols;
		
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
	*/

	

}
