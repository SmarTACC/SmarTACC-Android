package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.Tag;

import java.util.Arrays;

public class TagsManager extends TableManager<Tag> {

    private static final Field[] CUSTOM_FIELDS = {
            new Field("name", Field.TYPE_TEXT)
    };

    public TagsManager() {
        super("tags", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public Tag convert(Cursor cursor) {
        Tag tag = new Tag();

        tag.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        tag.name = cursor.getString(cursor.getColumnIndex("name"));

        return tag;
    }
}
