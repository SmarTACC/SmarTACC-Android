package com.ort.smartacc.data.managers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class MixedManager {
    private String name;

    public MixedManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Cursor query(SQLiteDatabase database, String[] columns, String selection, String[] selectionArgs, String sortOrder);
}
