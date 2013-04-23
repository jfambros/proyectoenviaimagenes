package com.amber.proyecto.envia.imagenes.sw.utils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class EnviaImagenHttp extends AsyncTask<Void, Void, Void>{
	private URL urlArch;
	private String nombreArchivo; 
    private FileInputStream fileInputStream;

	public EnviaImagenHttp(String url, String nombreArchivo, FileInputStream fileInputStream){

		try{
            urlArch = new URL(url);
            this.fileInputStream = fileInputStream;
            this.nombreArchivo=nombreArchivo;
            
    }catch(Exception ex){
        Log.i("HttpFileUpload","URL Malformatted");
    }
		
	}
	
	public void envia(FileInputStream fStream){
        fileInputStream = fStream;
        enviando();
}

private void enviando(){
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        try
        {
                Log.i(Tag,"Starting Http File Sending to URL");

                // Open a HTTP connection to the URL
                HttpURLConnection conn = (HttpURLConnection)urlArch.openConnection();

                // Allow Inputs
                conn.setDoInput(true);

                // Allow Outputs
                conn.setDoOutput(true);

                // Don't use a cached copy.
                conn.setUseCaches(false);

                // Use a post method.
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                 Log.i("nombre", fileInputStream.toString());                       
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + nombreArchivo +"\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.i(Tag,"Headers are written");

                // create a buffer of maximum size
                int bytesAvailable = fileInputStream.available();
                    
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[ ] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close streams
                fileInputStream.close();
                    
                dos.flush();
                    
                Log.i(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));
                     
                InputStream is = conn.getInputStream();
                    
                // retrieve the response from server
                int ch;

                StringBuffer b =new StringBuffer();
                while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                String s=b.toString();
                Log.i("Response",s);
                dos.close();
        }
        catch (MalformedURLException ex)
        {
                Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        }

        catch (IOException ioe)
        {
                Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }
}

@Override
protected Void doInBackground(Void... params) {
	enviando();
	return null;
}





}
