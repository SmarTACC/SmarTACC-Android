package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.Unit;

import java.util.Arrays;

public class UnitsManager extends TableManager<Unit> {

    private static final Field[] CUSTOM_FIELDS = {
            new Field("singular_name", Field.TYPE_TEXT),
            new Field("plural_name", Field.TYPE_TEXT)
    };

    public UnitsManager() {
        super("units", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public Unit convert(Cursor cursor) {
        Unit unit = new Unit();

        unit.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        unit.singular_name = cursor.getString(cursor.getColumnIndex("singular_name"));
        unit.plural_name = cursor.getString(cursor.getColumnIndex("plural_name"));

        return unit;
    }
}
