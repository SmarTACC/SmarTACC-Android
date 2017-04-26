package com.ort.smartacc.net;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SuggestTask extends AsyncTask<Void, Void, Boolean> {

    private static final String COOKIES_HEADER = "Set-Cookie";
    private static final String TOKEN_URL = "http://smartacc.proyectosort.edu.ar";
    private static final String SUGGEST_URL = "http://smartacc.proyectosort.edu.ar/suggestion";

    private String email;
    private String description;

    private OnSuggestListener listener;

    public SuggestTask(String email, String description, OnSuggestListener listener) {
        this.email = email;
        this.description = description;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String token = getToken(connection.getHeaderFields());
        if(token != null) {
            try {
                byte[] body = ("token=" + token + "&email=" + email + "&description=" + description).getBytes();

                connection = (HttpURLConnection) new URL(SUGGEST_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(body.length));
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(body);
                if(connection.getResponseCode() == 200) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (listener != null) {
            listener.onSuggest(success);
        }
    }

    private String getToken(Map<String, List<String>> headerFields) {
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        String token = null;

        if(cookiesHeader != null) {
            int i = 0;
            while (token == null && i < cookiesHeader.size()) {
                String cookieString = cookiesHeader.get(i);
                HttpCookie cookie = HttpCookie.parse(cookieString).get(0);

                if(cookie.getName().equals("XSRF-TOKEN")) {
                    token = cookie.getValue();
                }
                i++;
            }
        }

        return token;
    }

    public interface OnSuggestListener {
        void onSuggest(boolean success);
    }
}
