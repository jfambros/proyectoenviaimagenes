package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.File;

import android.os.Environment;

public class Variables {
	
	//public static final String HOST = "10.0.2.2";
	public static final String HOST = "192.168.1.201";
	public static final int tamArreglo = 16;
	public static final String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +File.separator;

}