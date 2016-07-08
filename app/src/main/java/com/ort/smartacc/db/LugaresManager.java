package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

public class LugaresManager extends TableManager{
    public LugaresManager() {
        super("lugares", "CREATE TABLE IF NOT EXISTS `lugares` (" +
                "  `idLugar` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  `lat` REAL NOT NULL," +
                "  `lon` REAL NOT NULL," +
                "  `name` varchar(20) NOT NULL," +
                "  `address` varchar(128) NOT NULL," +
                "  `description` varchar(512) NOT NULL," +
                "  `IDCategory` INTEGER" +
                ")");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);

        Lugar[] lugares = (new Gson()).fromJson(json, Lugar[].class);

        for(Lugar lugar:lugares) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("idLugar", lugar.idLugar);
            contentValues.put("lat", lugar.lat);
            contentValues.put("lon", lugar.lon);
            contentValues.put("name", lugar.name);
            contentValues.put("address", lugar.address);
            contentValues.put("description", lugar.description);
            contentValues.put("IDCategory", lugar.IDCategory);
            database.insert(name, null, contentValues);
        }
    }

    private class Lugar {
        long idLugar;
        double lat;
        double lon;
        String name;
        String address;
        String description;
        String IDCategory;
    }
}
