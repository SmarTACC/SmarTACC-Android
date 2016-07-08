package com.ort.smartacc.db;

import android.database.sqlite.SQLiteDatabase;

public abstract class TableManager {

    protected String name;
    protected String sqlCreate;

    public TableManager(String name, String sqlCreate){
        this.name = name;
        this.sqlCreate = sqlCreate;
    }

    public void create(SQLiteDatabase database){
        database.execSQL(sqlCreate);
    };
    public abstract void update(String json, SQLiteDatabase database);

    public void clear(SQLiteDatabase database){
        database.delete(name, null, null);
        //database.rawQuery("DELETE FROM " + name, null);
        //database.execSQL("vacuum");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSqlCreate() {
        return sqlCreate;
    }

    public void setSqlCreate(String sqlCreate) {
        this.sqlCreate = sqlCreate;
    }
}
