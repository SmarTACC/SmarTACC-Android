package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.RecipeTag;

import java.util.Arrays;

public class RecipesTagsManager extends TableManager<RecipeTag> {

    private static final Field[] CUSTOM_FIELDS = {
            new Field("recipe_id", Field.TYPE_TEXT),
            new Field("tag_id", Field.TYPE_TEXT)
    };

    public RecipesTagsManager() {
        super("recipes_tags", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public RecipeTag convert(Cursor cursor) {
        RecipeTag recipeTag = new RecipeTag();

        recipeTag.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        recipeTag.recipe_id = cursor.getLong(cursor.getColumnIndex("recipe_id"));
        recipeTag.tag_id = cursor.getLong(cursor.getColumnIndex("tag_id"));

        return recipeTag;
    }
}
