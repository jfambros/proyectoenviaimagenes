package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.File;

import android.os.Environment;

public class Variables {
	
	//public static final String HOST = "10.0.2.2";
	public static final String HOST = "192.168.1.64";
	public static final String tipoArchivo = ".jpg";
	public static final String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +File.separator;

}
