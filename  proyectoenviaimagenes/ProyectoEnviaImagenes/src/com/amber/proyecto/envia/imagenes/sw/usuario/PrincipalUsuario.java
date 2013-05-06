package com.amber.proyecto.envia.imagenes.sw.usuario;

import com.amber.proyecto.envia.imagenes.sw.R;
import com.amber.proyecto.envia.imagenes.sw.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class PrincipalUsuario extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principalusuario);
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
