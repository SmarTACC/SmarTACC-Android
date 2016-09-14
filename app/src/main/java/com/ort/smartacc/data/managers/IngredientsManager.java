package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.Ingredient;

import java.util.Arrays;

public class IngredientsManager extends TableManager<Ingredient> {
    private static final Field[] CUSTOM_FIELDS = {
            new Field("name", Field.TYPE_TEXT)
    };

    public IngredientsManager() {
        super("ingredients", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public Ingredient convert(Cursor cursor) {
        Ingredient ingredient = new Ingredient();

        ingredient.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        ingredient.name = cursor.getString(cursor.getColumnIndex("name"));

        return ingredient;
    }
}
