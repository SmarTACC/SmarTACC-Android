package com.ort.smartacc.data.provider;

import android.net.Uri;

public class ProviderContract {
    static final String AUTHORITY = "com.ort.smartacc.provider";
    static final String BASE_URI = "content://" + AUTHORITY + "/";

    public static Uri getUriForTable(String table) {
        return Uri.parse(BASE_URI + table);
    }

    public static Uri getUriForTableAndId(String table, long id) {
        return Uri.parse(BASE_URI + table + "/" + id);
    }
}
