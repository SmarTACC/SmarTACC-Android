package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

public class CategoriesManager extends TableManager{
    public CategoriesManager() {
        super("categories", "CREATE TABLE IF NOT EXISTS `categories` (" +
                "  `name` varchar(128) NOT NULL," +
                "  `IDCategory` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                ");");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);
        Category[] ingrecs = (new Gson()).fromJson(json, Category[].class);

        for(Category ingrec:ingrecs) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("IDCategory", ingrec.IDCategory);
            contentValues.put("name", !ingrec.name.equals("Carrefour") ? ingrec.name : "Supermercado");
            database.insert(name, null, contentValues);
        }
    }

    private class Category {
        long IDCategory;
        String name;
    }
}
