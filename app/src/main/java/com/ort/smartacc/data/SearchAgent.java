package com.ort.smartacc.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.ort.smartacc.data.managers.IngredientsManager;
import com.ort.smartacc.data.managers.RecipesManager;
import com.ort.smartacc.data.managers.TagsManager;
import com.ort.smartacc.data.model.Ingredient;
import com.ort.smartacc.data.model.Recipe;
import com.ort.smartacc.data.model.Tag;
import com.ort.smartacc.data.provider.ProviderContract;

import java.util.ArrayList;
import java.util.List;

public class SearchAgent {

    ContentResolver contentResolver;

    public SearchAgent(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public List<Recipe> searchRecipes(String name, List<Long> ingredientsIds, List<Long> tagsIds) {
        StringBuilder selectionBuilder = new StringBuilder();

        boolean isEmpty = true;

        if (name != null && !name.isEmpty()) {
            selectionBuilder.append("recipes.name LIKE '%").append(name).append("%'");
            isEmpty = false;
        }

        //TODO no usar los nombres de fields y tablas asi!
        if(ingredientsIds != null && !ingredientsIds.isEmpty()) {
            if(!isEmpty) {
                selectionBuilder.append(" AND ");
            }
            selectionBuilder.append("recipes_ingredients.ingredient_id IN (").append(TextUtils.join(",", ingredientsIds)).append(")");
        }

        if(tagsIds != null && !tagsIds.isEmpty()) {
            if(!isEmpty) {
                selectionBuilder.append(" AND ");
            }
            selectionBuilder.append("recipes_tags.tag_id IN (").append(TextUtils.join(",", tagsIds)).append(")");
        }

        Cursor cursor = contentResolver.query(ProviderContract.getUriForTable("search"),null, selectionBuilder.toString(), null, null);
        List<Recipe> recipes = new ArrayList<>();

        if(cursor != null) {
            RecipesManager recipesManager = new RecipesManager();
            while (cursor.moveToNext()) {
                recipes.add(recipesManager.convert(cursor));
            }
            cursor.close();
        }

        return recipes;
    }

    public List<Ingredient> getIngredients() {
        IngredientsManager ingredientsManager = new IngredientsManager();

        return ingredientsManager.getAll(contentResolver, "name");
    }

    public List<Tag> getTags() {
        TagsManager tagsManager = new TagsManager();

        return tagsManager.getAll(contentResolver, "name");
    }
}
