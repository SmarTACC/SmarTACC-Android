package com.ort.smartacc.net;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.ort.smartacc.Util;
import com.ort.smartacc.data.managers.TableManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TableUpdaterTask extends AsyncTask<Void, Void, Integer>{

    public final static int OK = 0;
    public final static int NETWORK_ERROR = 1;
    public final static int JSON_ERROR = 2;
    public final static int DB_ERROR = 3;
    public final static int WTF_ERROR = 4;

    private TableManager manager;
    private ContentProviderClient providerClient;
    private ContentResolver contentResolver;

    public TableUpdaterTask(TableManager manager, ContentProviderClient providerClient, ContentResolver contentResolver) {
        this.manager = manager;
        this.providerClient = providerClient;
        this.contentResolver = contentResolver;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(Util.API_URL + manager.getName()).openConnection();
            if(connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                inputStream.close();
                manager.update(result.toString(), providerClient, contentResolver);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return NETWORK_ERROR;
        } catch (JSONException jsone) {
            jsone.printStackTrace();
            return JSON_ERROR;
        } catch (RemoteException re) {
            re.printStackTrace();
            return DB_ERROR;
        }

        return OK;
    }
}
