package com.amber.proyecto.envia.imagenes.sw;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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

public class Mapa extends android.support.v4.app.FragmentActivity implements LocationListener{
	  //
	private ImageView ivAtrasMapa;
	private ImageView ivInicioMapa;
	private Button btnSatCalles;
	private boolean satelite = false;
	static final LatLng OAX = new LatLng(17.063021, -96.7202);
	  private GoogleMap map;
	  private Bundle bundle;
	  private ArrayList<ImagenParcelable> imagenes = new ArrayList<ImagenParcelable>();
	  private HashMap<Marker, ImagenParcelable> datosImagen = new HashMap<Marker, ImagenParcelable>();
	 private String queryFinal;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapa);

	    bundle = getIntent().getExtras();
	   imagenes = bundle.getParcelableArrayList("imagenes");
	   queryFinal = bundle.getString("queryFinal");
	   
	   btnSatCalles = (Button)findViewById(R.id.btnSateliteMapa);
	   btnSatCalles.setOnClickListener(btnSatCallesPres);
	   
	   ivAtrasMapa = (ImageView)findViewById(R.id.ivAtrasMapa);
	   ivAtrasMapa.setOnClickListener(ivAtrasMapaPres);
	   //imprimeLugares();
	   
	   ivInicioMapa = (ImageView)findViewById(R.id.ivInicioMapa);
	   ivInicioMapa.setOnClickListener(ivInicioMapaPres);
	   
	    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa))
	        .getMap();
	    
	    Marker oax = map.addMarker(new MarkerOptions().position(OAX)
		        .title("Oaxaca")
		         .snippet("Oaxaca de Juárez")
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

	    for (int i=0; i<imagenes.size(); i++){
			  Log.i("imagen #",i+".");
			  Marker mark = map.addMarker(new MarkerOptions().position(
					  new LatLng(imagenes.get(i).getLatitud(), imagenes.get(i).getLongitud()))
				.title("Categoría: "+imagenes.get(i).getNombreCategoria())
				.snippet(imagenes.get(i).getComentario())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
					  );
			  datosImagen.put(mark, imagenes.get(i));
			  
			  //map.addMarker(options)
		  }
	    
	    map.setMyLocationEnabled(true);
	    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
    


	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(OAX, 12));

	    map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
	    
	    map.setOnMarkerClickListener(markerListener);
	    map.setOnInfoWindowClickListener(info);
	    
	    
	  }
	  
	  private void limpiar(){
		  ivAtrasMapa = null;
		  ivInicioMapa = null;
		  System.gc();
	  }
	  
	  private OnClickListener ivInicioMapaPres = new OnClickListener() {

		public void onClick(View v) {
			System.gc();
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(Mapa.this, Principal.class);
			startActivity(intent);

		}
	};
	  
	  private OnClickListener ivAtrasMapaPres = new OnClickListener() {
		public void onClick(View v) {
			System.gc();
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(Mapa.this, Busca.class);
			startActivity(intent);

		}
	};
	  
	  private OnClickListener btnSatCallesPres = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.gc();
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
				limpiar();
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("datosImagen", datosImagen.get(marker));
				intent.putExtra("queryFinal", queryFinal);
				intent.setClass(Mapa.this, MuestraInfo.class);
				startActivity(intent);
			}
		}
	};




	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		 
        // Getting longitude of the current location
        double longitude = location.getLongitude();
 
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}}
