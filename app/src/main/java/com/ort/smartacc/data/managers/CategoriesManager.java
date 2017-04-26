package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.Category;

import java.util.Arrays;

public class CategoriesManager extends TableManager<Category> {
    private static final Field[] CUSTOM_FIELDS = {
            new Field("name", Field.TYPE_TEXT)
    };

    public CategoriesManager() {
        super("categories", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public Category convert(Cursor cursor) {
        Category category = new Category();

        category.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        category.name = cursor.getString(cursor.getColumnIndex("name"));

        return category;
    }
}
