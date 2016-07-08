package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

public class IngredientesManager extends TableManager{
    public IngredientesManager() {
        super("ingredientes", "CREATE TABLE IF NOT EXISTS `ingredientes` (" +
                "  `IDIng` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  `Nombre` varchar(40) NOT NULL" +
                ");");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);

        Ingrediente[] ingredientes = (new Gson()).fromJson(json, Ingrediente[].class);

        for(Ingrediente ingrediente:ingredientes) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("IDIng", ingrediente.IDIng);
            contentValues.put("Nombre", ingrediente.Nombre);
            database.insert(name, null, contentValues);
        }
    }

    private class Ingrediente{
        long IDIng;
        String Nombre;
    }
}
