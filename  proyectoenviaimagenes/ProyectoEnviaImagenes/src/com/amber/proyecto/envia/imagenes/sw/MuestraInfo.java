package com.amber.proyecto.envia.imagenes.sw;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.amber.proyecto.envia.imagenes.sw.utils.ImagenParcelable;
import com.amber.proyecto.envia.imagenes.sw.utils.MuestraImagen;
import com.amber.proyecto.envia.imagenes.sw.utils.Variables;

public class MuestraInfo extends Activity{
	private Bundle bundle;
	private ImagenParcelable datosImagen = new ImagenParcelable();
	private String ruta = "http://"+Variables.HOST+"/pags/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muestrainfo);
		bundle = getIntent().getExtras();
		datosImagen = bundle.getParcelable("datosImagen");
		new MuestraImagen((ImageView)findViewById(R.id.ivImagenMuestraInfo)).execute(ruta+datosImagen.getNombreImagen());
		
			        
		Log.i("nombreImagen ",datosImagen.getNombreImagen()+" "+datosImagen.getNombreCategoria());
	}
}
