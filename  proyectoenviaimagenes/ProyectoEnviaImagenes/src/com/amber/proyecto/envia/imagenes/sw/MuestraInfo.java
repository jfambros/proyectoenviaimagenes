package com.amber.proyecto.envia.imagenes.sw;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MuestraInfo extends Activity{
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muestrainfo);
		
		bundle = getIntent().getExtras();
		Log.i("nombreImagen ",bundle.getString("nombreImagen"));
	}
}
