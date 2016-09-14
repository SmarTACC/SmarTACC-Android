package com.ort.smartacc.data.managers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SearchManager extends MixedManager {

    public SearchManager() {
        super("search");
    }

    @Override
    public Cursor query(SQLiteDatabase database, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        TagsManager tagsManager = new TagsManager();
        IngredientsManager ingredientsManager = new IngredientsManager();

        StringBuilder queryBuilder = new StringBuilder("SELECT recipes._ID, COUNT(*) AS count, recipes.name FROM recipes");

        int tagsQuantity = argsQuantity(selection, tagsManager.getName());
        int ingredientsQuantity = argsQuantity(selection, ingredientsManager.getName());

        if(tagsQuantity > 0) {
            RecipesTagsManager recipesTagsManager = new RecipesTagsManager();
            //TODO: no acceder a los fields de forma tan poco directa!
            queryBuilder.append(" INNER JOIN ").append(recipesTagsManager.getName()).append(" ON recipes._ID = recipes_tags.recipe_id");
        }

        if(ingredientsQuantity > 0) {
            RecipesIngredientsManager recipesIngredientsManager = new RecipesIngredientsManager();
            queryBuilder.append(" INNER JOIN ").append(recipesIngredientsManager.getName()).append(" ON recipes._ID = recipes_ingredients.recipe_id");
        }

        queryBuilder.append(" WHERE ").append(selection).append(" GROUP BY recipes._ID");

        if(ingredientsQuantity > 0 && tagsQuantity > 0) {
            queryBuilder.append(" HAVING count = ").append(ingredientsQuantity * tagsQuantity);
        } else if(ingredientsQuantity > 0 || tagsQuantity > 0) {
            queryBuilder.append(" HAVING count = ").append(ingredientsQuantity + tagsQuantity);
        }

        queryBuilder.append(" ORDER BY ").append((sortOrder != null && !sortOrder.isEmpty())? sortOrder : "recipes.name;");

        return database.rawQuery(queryBuilder.toString(), selectionArgs);
    }

    private int argsQuantity(String selection, String which) {
        int quantity = 0;

        int indexOfArg = selection.indexOf(which);
        if(indexOfArg >= 0) {
            int start = selection.indexOf("(", indexOfArg);
            int end = selection.indexOf(")", indexOfArg);

            quantity = selection.substring(start, end).split(",").length;
        }

        return quantity;
    }
}
