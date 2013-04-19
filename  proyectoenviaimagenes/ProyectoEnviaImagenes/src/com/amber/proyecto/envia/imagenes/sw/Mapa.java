package com.amber.proyecto.envia.imagenes.sw;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa extends android.support.v4.app.FragmentActivity {
	  static final LatLng OAX = new LatLng(17.071554, -96.71917);
	  private GoogleMap map;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapa);

	   
	    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa))
	        .getMap();
	    Marker hamburg = map.addMarker(new MarkerOptions().position(OAX)
	        .title("Oaxaca")
	        .icon(BitmapDescriptorFactory
	            .fromResource(R.drawable.ic_launcher)));

	    // Move the camera instantly to hamburg with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(OAX, 15));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	    
	  }

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {

	    return true;
	  }

}
