package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexiones {
	
	public static boolean respondeServidor(String url)
	{
	      try
	      {          
	            HttpGet request = new HttpGet(url);
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy()
	            {
	                  @Override
	                  public long getKeepAliveDuration(HttpResponse response, HttpContext context)
	                  {
	                           return 0;
	                  }
	            });
	            HttpResponse response = httpClient.execute(request);
	            return response.getStatusLine().getStatusCode() == 200;

	      }
	      catch (IOException e){}
	      return false;
	}
	
	public static boolean conexionInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		return true;
		}

		return false;
	}

}
