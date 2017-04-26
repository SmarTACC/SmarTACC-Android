package com.ort.smartacc.data.managers;

import android.database.Cursor;

import com.ort.smartacc.data.model.RecipeIngredient;

import java.util.Arrays;

public class RecipesIngredientsManager extends TableManager<RecipeIngredient> {

    private static final Field[] CUSTOM_FIELDS = {
            new Field("recipe_id", Field.TYPE_INTEGER),
            new Field("ingredient_id", Field.TYPE_INTEGER),
            new Field("unit_id", Field.TYPE_INTEGER),
            new Field("amount", Field.TYPE_REAL)
    };

    public RecipesIngredientsManager() {
        super("recipes_ingredients", Arrays.asList(CUSTOM_FIELDS));
    }

    @Override
    public RecipeIngredient convert(Cursor cursor) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();

        recipeIngredient.id = cursor.getLong(cursor.getColumnIndex(DATABASE_ID));
        recipeIngredient.recipe_id = cursor.getLong(cursor.getColumnIndex("recipe_id"));
        recipeIngredient.ingredient_id = cursor.getLong(cursor.getColumnIndex("ingredient_id"));
        recipeIngredient.unit_id = cursor.getLong(cursor.getColumnIndex("unit_id"));
        recipeIngredient.amount = cursor.getDouble(cursor.getColumnIndex("amount"));

        return recipeIngredient;
    }
}
