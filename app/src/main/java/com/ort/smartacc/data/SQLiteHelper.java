package com.ort.smartacc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ort.smartacc.data.managers.TableManager;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smartacc";
    private static final int DB_VERSION = 14;

    Context context;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
        //TODO agregar json locales que se pongan la primera vez que se usa la app?
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if(oldVersion < 13) {
            clearFirstVersionDatabase(database);
        } else {
            if(oldVersion < 14) {
                // Se agregaron los videos en la tabla recipes
                database.execSQL("DROP TABLE IF EXISTS recipes");
            }
        }

        createTables(database);
    }

    private void clearFirstVersionDatabase(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS modifications");
        database.execSQL("DROP TABLE IF EXISTS categories");
        database.execSQL("DROP TABLE IF EXISTS ingrec");
        database.execSQL("DROP TABLE IF EXISTS ingredientes");
        database.execSQL("DROP TABLE IF EXISTS lugares");
        database.execSQL("DROP TABLE IF EXISTS recetas");
        database.execSQL("DROP TABLE IF EXISTS tagrec");
        database.execSQL("DROP TABLE IF EXISTS tags");
    }

    private void createTables(SQLiteDatabase database) {
        ManagersCollection managersCollection = new ManagersCollection();
        for(TableManager manager: managersCollection.getTableManagers()){
            database.execSQL(manager.getSqlCreate());
        }
    }
}