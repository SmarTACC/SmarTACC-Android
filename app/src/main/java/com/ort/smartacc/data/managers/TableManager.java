package com.ort.smartacc.data.managers;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ort.smartacc.data.provider.ProviderContract;
import com.ort.smartacc.data.model.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TableManager<T extends Table> {
    public static final String DATABASE_ID ="_ID";
    public static final String JSON_ID ="id";

    protected String name;
    private List<Field> fields;

    private static final Field[] DEFAULT_FIELDS = {
            new Field("created_at", Field.TYPE_TEXT),
            new Field("updated_at", Field.TYPE_TEXT),
            new Field("deleted_at", Field.TYPE_TEXT)
    };

    public TableManager(String name, List<Field> customFields){
        this.name = name;

        fields = new ArrayList<>(customFields);
        fields.addAll(Arrays.asList(DEFAULT_FIELDS));
    }

    public void update(String json, ContentProviderClient providerClient, ContentResolver contentResolver) throws JSONException, RemoteException {

        JSONArray array = new JSONArray(json);

        ContentValues[] contentValuesList = new ContentValues[array.length()];
        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put(DATABASE_ID, object.optLong(JSON_ID));
            for(Field field: fields) {
                switch (field.type) {
                    case Field.TYPE_INTEGER:
                        contentValues.put(field.name, object.optLong(field.name));
                        break;
                    case Field.TYPE_REAL:
                        contentValues.put(field.name, object.optDouble(field.name));
                        break;
                    case Field.TYPE_BOOLEAN:
                        contentValues.put(field.name, object.optBoolean(field.name));
                        break;
                    case Field.TYPE_TEXT:
                    default:
                        contentValues.put(field.name, object.optString(field.name));
                        break;
                }
            }

            contentValuesList[i] = contentValues;
        }

        clear(providerClient);

        Uri uri = ProviderContract.getUriForTable(name);
        providerClient.bulkInsert(uri, contentValuesList);
        contentResolver.notifyChange(uri, null);
    }

    public String getName() {
        return name;
    }

    public String getSqlCreate() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS '");
        builder.append(name)
                .append("' ('_ID' INTEGER PRIMARY KEY, ");

        List<String> rows = new ArrayList<>();
        for(Field field : fields){
            rows.add("'" + field.name + "' " + field.type);
        }
        builder.append(TextUtils.join(", ", rows)).append(");");

        return builder.toString();
    }

    public List<T> getAll(ContentResolver contentResolver) {
        return getAll(contentResolver, null);
    }

    public List<T> getAll(ContentResolver contentResolver, String sortOrder) {
        Cursor cursor = contentResolver.query(ProviderContract.getUriForTable(name), null, null, null, sortOrder);

        List<T> list = new ArrayList<>();

        if(cursor != null) {
            while (cursor.moveToNext()) {
                list.add(convert(cursor));
            }

            cursor.close();
        }

        return list;
    }

    public T getById(ContentResolver contentResolver, long id) {
        Cursor cursor = contentResolver.query(ProviderContract.getUriForTableAndId(name, id), null, null, null, null);

        T item = null;

        if(cursor != null && cursor.moveToFirst()) {
            item = convert(cursor);

            cursor.close();
        }

        return item;
    }

    public List<T> search(ContentResolver contentResolver, String selection, String[] selectionArgs) {
        Cursor cursor = contentResolver.query(ProviderContract.getUriForTable(name), null, selection, selectionArgs, null);

        List<T> list = new ArrayList<>();

        if(cursor != null) {
            while (cursor.moveToNext()) {
                list.add(convert(cursor));
            }

            cursor.close();
        }

        return list;
    }

    public abstract T convert(Cursor cursor);

    protected Long safeGetLong(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return (index >= 0) ? cursor.getLong(index) : 0L;
    }

    protected String safeGetString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return (index >= 0) ? cursor.getString(index) : null;
    }

    protected Double safeGetDouble(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return (index >= 0) ? cursor.getDouble(index) : 0.0;
    }

    private void clear(ContentProviderClient providerClient) throws RemoteException {
        providerClient.delete(ProviderContract.getUriForTable(name), null, null);
    }

    public static class Field {

        public static final String TYPE_INTEGER = "INTEGER";
        public static final String TYPE_TEXT = "TEXT";
        public static final String TYPE_REAL = "REAL";
        public static final String TYPE_BOOLEAN = "BOOLEAN";
        String name;
        String type;

        public Field(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

}
