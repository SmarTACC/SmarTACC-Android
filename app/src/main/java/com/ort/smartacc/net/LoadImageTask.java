package com.ort.smartacc.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.ort.smartacc.Util;

import java.io.InputStream;

/**
 * AsyncTask para cargar imagenes en los ImageView (principalmente imagenes de recetas).
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
    Context context;
    ImageView bmImage;
    OnReadyCallback callback;

    /**
     * Constructor
     * @param bmImage ImageView a llenar.
     */
    public LoadImageTask(Context context,ImageView bmImage, OnReadyCallback callback) {
        this.context = context;
        this.bmImage = bmImage;
        this.callback = callback;
    }

    /**
     * Task principal. Intentará conseguir la imagen del servidor.
     * En caso de no poder conectar, no hará nada.
     * @param urls Dirección de la imagen en el servidor.
     * @return Bitmap, imagen conseguida.
     */
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        if(Util.canConnect(context)) {
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mIcon11;
    }

    /**
     * Acción ejecutada luego de doInBackground. Setea la imagen en el ImageView.
     * @param result Bitmap conseguido del servidor, null en caso de no poder conectar.
     */
    protected void onPostExecute(Bitmap result) {
        if(result!=null) {
            bmImage.setImageBitmap(result);
            callback.saveBitmap(result);
        }
    }

    public interface OnReadyCallback{
        void saveBitmap(Bitmap bitmap);
    }
}