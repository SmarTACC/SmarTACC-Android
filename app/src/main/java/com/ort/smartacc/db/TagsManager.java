package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

public class TagsManager extends TableManager{
    public TagsManager() {
        super("tags", "CREATE TABLE IF NOT EXISTS `tags` (" +
                "  `IDTag` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  `Nombre` varchar(20) NOT NULL" +
                ");");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);

        Receta[] tags = (new Gson()).fromJson(json, Receta[].class);

        for(Receta tag:tags) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("IDTag", tag.IDTag);
            contentValues.put("Nombre", tag.Nombre);
            database.insert(name, null, contentValues);
        }
    }

    private class Receta{
        long IDTag;
        String Nombre;
    }
}
