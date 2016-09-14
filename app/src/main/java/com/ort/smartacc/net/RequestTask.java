package com.ort.smartacc.net;

import android.content.Context;
import android.os.AsyncTask;

import com.ort.smartacc.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask usado para mandar requerimientos HTTP.
 */
//TODO eliminar?
public class RequestTask extends AsyncTask<String, Void, HttpResponse> {
    Context context;
    OnReadyCallback callback;
    String requestMethod;
    public String url;
    byte[] body;

    public RequestTask(Context context,String url){
        super();
        this.context=context;
        this.url = url;
        this.callback = null;
        this.requestMethod = "GET";
    }
    public RequestTask(Context context,String url, OnReadyCallback callback){
        super();
        this.context=context;
        this.url = url;
        this.callback = callback;
        this.requestMethod = "GET";
    }
    public RequestTask(Context context,String url, String requestMethod, String body, OnReadyCallback callback){
        super();
        this.context=context;
        this.url = url;
        this.callback = callback;
        this.requestMethod = requestMethod;
        this.body = body.getBytes();
    }
    /**
     * Task principal. Manda el requerimiento a la url pasada como parámetro y devuelve la respuesta del servidor.
     * @return Respuesta del servidor.
     */
    @Override
    protected HttpResponse doInBackground(String... params) {
        String responseString = "";
        int responseCode = 400;
        HttpURLConnection connection=null;
        try {
            if(Util.canConnect(context)) {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod( requestMethod );
                if(body != null){
                    connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty( "charset", "utf-8");
                    connection.setRequestProperty( "Content-Length", Integer.toString( body.length ));
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.write(body);
                }
                responseCode = connection.getResponseCode();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                inputStream.close();
                responseString = result.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return new HttpResponse(responseCode, responseString);
    }

    /**
     * Ejecutado luego del doInBackground.
     * Devuelve la respuesta del servidor al callback.
     * @param result Respuesta del servidor.
     */
    @Override
    protected void onPostExecute(HttpResponse result) {
        if(callback != null) {
            callback.onReady(result);
        }
    }

    public interface OnReadyCallback{
        void onReady(HttpResponse response);
    }
}