package com.ort.smartacc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

public class TagRecManager extends TableManager{

    public TagRecManager() {
        super("tagrec", "CREATE TABLE IF NOT EXISTS `tagrec` (" +
                "  `IDReceta` INTEGER NOT NULL," +
                "  `IDTag` INTEGER NOT NULL," +
                "  `IDTagrec` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
                ");");
    }

    @Override
    public void update(String json, SQLiteDatabase database) {
        super.clear(database);

        TagRec[] tagrecs = (new Gson()).fromJson(json, TagRec[].class);

        for(TagRec tagrec:tagrecs) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("IDTag", tagrec.IDTag);
            contentValues.put("IDReceta", tagrec.IDReceta);
            contentValues.put("IDTagrec", tagrec.IDTagrec);
            database.insert(name, null, contentValues);
        }
    }

    private class TagRec {
        long IDTag;
        String IDReceta;
        int IDTagrec;
    }
}
