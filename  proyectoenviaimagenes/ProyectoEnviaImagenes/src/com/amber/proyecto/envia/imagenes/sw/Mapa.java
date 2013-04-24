package com.amber.proyecto.envia.imagenes.sw;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amber.proyecto.envia.imagenes.sw.utils.ImagenParcelable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa extends android.support.v4.app.FragmentActivity{
	  //
	private ImageView ivAtrasMapa;
	private Button btnSatCalles;
	private boolean satelite = false;
	static final LatLng OAX = new LatLng(17.063021, -96.7202);
	  private GoogleMap map;
	  private Bundle bundle;
	  private ArrayList<ImagenParcelable> imagenes = new ArrayList<ImagenParcelable>();
	  private HashMap<Marker, ImagenParcelable> datosImagen = new HashMap<Marker, ImagenParcelable>();

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapa);

	    bundle = getIntent().getExtras();
	   imagenes = bundle.getParcelableArrayList("imagenes");
	   
	   btnSatCalles = (Button)findViewById(R.id.btnSateliteMapa);
	   btnSatCalles.setOnClickListener(btnSatCallesPres);
	   
	   ivAtrasMapa = (ImageView)findViewById(R.id.ivAtrasMapa);
	   ivAtrasMapa.setOnClickListener(ivAtrasMapaPres);
	   //imprimeLugares();
	   
	    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa))
	        .getMap();
	    
	    Marker oax = map.addMarker(new MarkerOptions().position(OAX)
		        .title("Oaxaca")
		         .snippet("Oaxaca de Juárez")
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

	    for (int i=0; i<imagenes.size(); i++){
			  
			  Marker mark = map.addMarker(new MarkerOptions().position(
					  new LatLng(imagenes.get(i).getLatitud(), imagenes.get(i).getLongitud()))
				.title("Categoría: "+imagenes.get(i).getNombreCategoria())
				.snippet(imagenes.get(i).getComentario())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
					  );
			  datosImagen.put(mark, imagenes.get(i));
			  
			  //map.addMarker(options)
		  }
	    
	    


	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(OAX, 12));

	    map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
	    
	    map.setOnMarkerClickListener(markerListener);
	    map.setOnInfoWindowClickListener(info);
	    
	    
	  }
	  
	  private OnClickListener ivAtrasMapaPres = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(Mapa.this, Busca.class);
			startActivity(intent);
		}
	};
	  
	  private OnClickListener btnSatCallesPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (satelite == false){
				btnSatCalles.setText("  Mapa  ");
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				satelite = true;
			}
			else{
				btnSatCalles.setText("Satélite");
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				satelite = false;
			}
			
		}
	};
	  
	  private OnMarkerClickListener markerListener = new OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker marker) {
			Toast.makeText(Mapa.this, marker.getSnippet(), Toast.LENGTH_LONG).show();
			return false;
		}
	};
	
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {

	    return true;
	  }

	  private OnInfoWindowClickListener info = new OnInfoWindowClickListener() {
		
		@Override
		public void onInfoWindowClick(Marker marker) {
			//Log.i("Seleccionado ", datosImagen.get(marker).getNombreImagen());
			if (!marker.getId().equals("m0")){
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(Mapa.this, MuestraInfo.class);
				intent.putExtra("datosImagen", datosImagen.get(marker));
				intent.putParcelableArrayListExtra("imagenes", imagenes);
				startActivity(intent);
				finish();
			}
		}
	};

}
