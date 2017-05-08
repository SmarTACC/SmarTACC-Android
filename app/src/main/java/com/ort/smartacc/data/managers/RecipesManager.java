package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.Recipe;

import java.util.Arrays;

public class RecipesManager extends TableManager<Recipe> {

    private static final Field[] CUSTOM_FIELDS = {
            new Field("name", Field.TYPE_TEXT),
            new Field("description", Field.TYPE_TEXT),
            new Field("preparation_time", Field.TYPE_INTEGER),
            new Field("calories", Field.TYPE_INTEGER),
            new Field("portions", Field.TYPE_REAL),
            new Field("youtube_url", Field.TYPE_TEXT)
    };

    public RecipesManager() {
        super("recipes", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public Recipe convert(Cursor cursor) {
        Recipe recipe = new Recipe();

        recipe.id = safeGetLong(cursor, DATABASE_ID);
        recipe.name = safeGetString(cursor, "name");
        recipe.description = safeGetString(cursor, "description");
        recipe.preparation_time = safeGetLong(cursor, "preparation_time");
        recipe.calories = safeGetLong(cursor, "calories");
        recipe.portions = safeGetDouble(cursor, "portions");
        recipe.youtube_url = safeGetString(cursor, "youtube_url");

        return recipe;
    }
}
