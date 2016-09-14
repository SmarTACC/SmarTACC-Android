package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.Place;

import java.util.Arrays;

public class PlacesManager extends TableManager<Place> {
    private static final Field[] CUSTOM_FIELDS = {
            new Field("name", Field.TYPE_TEXT),
            new Field("address", Field.TYPE_TEXT),
            new Field("description", Field.TYPE_TEXT),
            new Field("website", Field.TYPE_TEXT),
            new Field("phone", Field.TYPE_TEXT),
            new Field("lat", Field.TYPE_REAL),
            new Field("lon", Field.TYPE_REAL),
            new Field("category_id", Field.TYPE_INTEGER),
    };

    public PlacesManager() {
        super("places", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public Place convert(Cursor cursor) {
        Place place = new Place();

        place.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        place.name = cursor.getString(cursor.getColumnIndex("name"));
        place.address = cursor.getString(cursor.getColumnIndex("address"));
        place.description = cursor.getString(cursor.getColumnIndex("description"));
        place.website = cursor.getString(cursor.getColumnIndex("website"));
        place.phone = cursor.getString(cursor.getColumnIndex("phone"));
        place.lat = cursor.getDouble(cursor.getColumnIndex("lat"));
        place.lon = cursor.getDouble(cursor.getColumnIndex("lon"));
        place.category_id = cursor.getLong(cursor.getColumnIndex("category_id"));

        return place;
    }
}
