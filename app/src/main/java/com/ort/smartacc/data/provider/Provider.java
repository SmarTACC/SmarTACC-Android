package com.ort.smartacc.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ort.smartacc.data.ManagersCollection;
import com.ort.smartacc.data.SQLiteHelper;
import com.ort.smartacc.data.managers.MixedManager;
import com.ort.smartacc.data.managers.SearchManager;


public class Provider extends ContentProvider {

    SQLiteHelper helper;
    ManagersCollection managersCollection;
    MixedManager[] mixedManagers = {
            new SearchManager()
    };
    UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        helper = new SQLiteHelper(getContext());
        managersCollection = new ManagersCollection();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        int i;
        //TODO rehacer el mapping
        for(i = 0; i < managersCollection.getSize(); i++) {
            // Los codes pares se usan para consultar una tabla completa
            // Los codes impares para consultar una row concreta
            // Se puede saber la tabla a la que va dirigido haciendo code/2 (división entera)
            uriMatcher.addURI(ProviderContract.AUTHORITY, managersCollection.getTableManager(i).getName(), i * 2);
            uriMatcher.addURI(ProviderContract.AUTHORITY, managersCollection.getTableManager(i).getName() + "/#", (i * 2) + 1);
        }

        i *= 2;
        for(MixedManager mixedManager:mixedManagers) {
            uriMatcher.addURI(ProviderContract.AUTHORITY, mixedManager.getName(), i);
            i++;
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);

        if(match == UriMatcher.NO_MATCH) {
            throw  new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if(match % 2 == 1) {
            selection = appendId(selection, uri);
        }

        SQLiteDatabase database = helper.getReadableDatabase();

        if(match < managersCollection.getSize() * 2) {
            String databaseName = managersCollection.getTableManager(match / 2).getName();

            return database.query(databaseName, columns, selection, selectionArgs, null, null, sortOrder);
        } else {
            match -= managersCollection.getSize() * 2;

            return mixedManagers[match].query(database, columns, selection, selectionArgs, sortOrder);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        int match = uriMatcher.match(uri);

        if(match == -1 || match % 2 == 1) {
            throw new UnsupportedOperationException("Cannot insert to this URI: " + uri);
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        String databaseName = managersCollection.getTableManager(match/2).getName();

        long rowId = database.insertWithOnConflict(databaseName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        if(match == -1) {
            throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if(match % 2 == 1) {
            selection = appendId(selection, uri);
        }

        SQLiteDatabase database = helper.getReadableDatabase();
        String databaseName = managersCollection.getTableManager(match/2).getName();

        return database.delete(databaseName, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        if(match == -1) {
            throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if(match % 2 == 1) {
            selection = appendId(selection, uri);
        }

        SQLiteDatabase database = helper.getReadableDatabase();
        String databaseName = managersCollection.getTableManager(match/2).getName();

        return database.update(databaseName, contentValues, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Cannot get type.");
    }

    private String appendId(String selection,  Uri uri){

        if(selection == null) {
            selection = "";
        }

        if(!selection.equals("")){
            selection += "&";
        }

        return selection + "_ID = " + uri.getLastPathSegment();
    }
}
