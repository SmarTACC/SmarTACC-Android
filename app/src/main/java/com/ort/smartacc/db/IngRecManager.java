package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

public class IngRecManager extends TableManager{

    public IngRecManager() {
        super("ingrec", "CREATE TABLE IF NOT EXISTS `ingrec` (" +
                "  `IDIng` INTEGER NOT NULL," +
                "  `IDRecetas` INTEGER NOT NULL," +
                "  `Cantidad` float NOT NULL," +
                "  `Unidad` varchar(30) NOT NULL," +
                "  `IDIngrec` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                ");");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);
        IngRec[] ingrecs = (new Gson()).fromJson(json, IngRec[].class);

        for(IngRec ingrec:ingrecs) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("IDIng", ingrec.IDIng);
            contentValues.put("IDRecetas", ingrec.IDRecetas);
            contentValues.put("Cantidad", ingrec.Cantidad);
            contentValues.put("Unidad", ingrec.Unidad);
            contentValues.put("IDIngrec", ingrec.IDIngrec);
            database.insert(name, null, contentValues);
        }
    }

    private class IngRec {
        long IDIng;
        String IDRecetas;
        String Cantidad;
        String Unidad;
        int IDIngrec;
    }
}
