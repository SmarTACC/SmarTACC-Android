package com.ort.smartacc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Esta clase está pensaba para meter funciones que pueden ser utiles para distintas ocaciones.
 * La idea es evitar escribirlas en más de una clase.
 */
public class Util {
    /**
     * Dirección básica del servidor.
     */
    public static final String API_URL = "http://smartacc.proyectosort.edu.ar/api/";
    //TODO hacer una función que te devuelva el string completo pasandole el id de imagen
    public static final String IMAGES_URL = "http://smartacc.proyectosort.edu.ar/img/recipes/";
    public static final String TOKEN_URL = "http://smartacc.proyectosort.edu.ar";
    public static final String SUGGEST_URL = "http://smartacc.proyectosort.edu.ar/suggestion";
    public static final String OLD_SERVER_URL = "http://www.ort.edu.ar:8018/";

    public static final String SERVICE_BROADCAST_ACTION = "com.ort.smartacc.SERVICE_READY_BROADCAST";
    public static final String SERVICE_STATUS = "com.ort.smartacc.SERVICE_READY_STATUS";

    /**
     * Función que verifica el poder conectarse a internet
     * @param ctx El contexto usado para conseguir el servicio para checkear el estado de la red.
     * @return boolean, true en caso de poder conectarse una red, false en el caso contrario.
     */
    public static boolean canConnect(Context ctx){
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
