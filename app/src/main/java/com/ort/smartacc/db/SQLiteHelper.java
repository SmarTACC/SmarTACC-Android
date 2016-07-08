package com.ort.smartacc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ort.smartacc.Util;
import com.ort.smartacc.net.HttpRequestPool;
import com.ort.smartacc.net.HttpResponse;
import com.ort.smartacc.net.RequestTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "smartacc";

    Context context;

    public final static TableManager[] TABLE_MANAGERS = {
            new RecetasManager(),
            new TagsManager(),
            new IngredientesManager(),
            new TagRecManager(),
            new IngRecManager(),
            new LugaresManager(),
            new CategoriesManager()
    };

    HashMap<String, Integer> MANAGER_INDEX;

    final static String CREATE_MODIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS modifications (" +
            "    table_name TEXT NOT NULL PRIMARY KEY ON CONFLICT REPLACE," +
            "    changed_at TEXT NOT NULL" +
            ");";

    public SQLiteHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
        this.context = context;

        MANAGER_INDEX = new HashMap<>();
        for(int i=0; i<TABLE_MANAGERS.length; i++){
            MANAGER_INDEX.put(TABLE_MANAGERS[i].getName(), i);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MODIFICATION_TABLE);
        for(TableManager manager:TABLE_MANAGERS){
            ContentValues contentValues = new ContentValues();
            contentValues.put("table_name", manager.getName());
            contentValues.put("changed_at", "-");
            db.insert("modifications", null, contentValues);

            manager.create(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS modifications");
        db.execSQL(CREATE_MODIFICATION_TABLE);

        for(TableManager manager:TABLE_MANAGERS){
            db.execSQL("DROP TABLE IF EXISTS " + manager.getName());

            ContentValues contentValues = new ContentValues();
            contentValues.put("table_name", manager.getName());
            contentValues.put("changed_at", "-");
            db.insert("modifications", null, contentValues);

            manager.create(db);
        }
    }

    public void update(final SQLiteDatabase db){
        //Update iniciado
        try {
            //Descargo el integrity check para comprobar que se tenga la misma cantidad de rows
            //y la fecha sea la correcta
            HttpResponse dbIntegrity = new RequestTask(context, Util.SERVER_URL + "smartacc/json/dbintegritycheck.php").execute().get();
            if(dbIntegrity.code == 200){
                //Creo el pool para paralelizar las llamadas al server
                HttpRequestPool pool = new HttpRequestPool();
                //Cargo la tabla de modificaciones
                JSONObject obj = new JSONObject(dbIntegrity.response);
                Cursor c = db.query("modifications", null, null, null, null, null, null);

                while(!c.isLast()){
                    c.moveToNext();

                    final String tableName = c.getString(c.getColumnIndex("table_name"));
                    final JSONObject jsonTable = obj.getJSONObject(tableName);

                    final String lastUpdateTime = jsonTable.getString("update");

                    //Compruebo la integridad de esa tabla
                    if(!lastUpdateTime.equals(c.getString(c.getColumnIndex("changed_at")))
                            || jsonTable.getInt("cantidad") != db.query(tableName, null, null, null, null, null, null).getCount()){

                        //En caso de no estar integra, la descargo
                        RequestTask task = new RequestTask(context, Util.SERVER_URL + "smartacc/json/" + tableName + ".php", null);
                        pool.add(tableName, task);
                    }
                }
                c.close();

                Map<String, HttpResponse> responses = pool.runAndWait();
                for(Map.Entry<String, HttpResponse> entry : responses.entrySet()){

                    String tableName = entry.getKey();
                    HttpResponse response = entry.getValue();

                    if(response.code == 200){
                        TABLE_MANAGERS[MANAGER_INDEX.get(tableName)].update(response.response, db);

                        String lastUpdateTime = obj.getJSONObject(tableName).getString("update");

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("table_name", tableName);
                        contentValues.put("changed_at", lastUpdateTime);
                        db.insert("modifications", null, contentValues);
                    }
                }
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}