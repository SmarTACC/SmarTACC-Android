package com.ort.smartacc.data;

import android.content.ContentResolver;
import android.content.Context;

import com.ort.smartacc.data.managers.IngredientsManager;
import com.ort.smartacc.data.managers.RecipesIngredientsManager;
import com.ort.smartacc.data.managers.RecipesManager;
import com.ort.smartacc.data.managers.RecipesTagsManager;
import com.ort.smartacc.data.managers.TagsManager;
import com.ort.smartacc.data.managers.UnitsManager;
import com.ort.smartacc.data.model.Ingredient;
import com.ort.smartacc.data.model.Recipe;
import com.ort.smartacc.data.model.RecipeIngredient;
import com.ort.smartacc.data.model.RecipeTag;
import com.ort.smartacc.data.model.Tag;
import com.ort.smartacc.data.model.Unit;

import java.util.ArrayList;
import java.util.List;

public class RecipesAgent {

    ContentResolver contentResolver;

    RecipesManager recipesManager;

    RecipesIngredientsManager recipesIngredientsManager;
    IngredientsManager ingredientsManager;
    UnitsManager unitsManager;
    RecipesTagsManager recipesTagsManager;
    TagsManager tagsManager;

    public RecipesAgent(Context context) {
        contentResolver = context.getContentResolver();

        recipesManager = new RecipesManager();

        recipesIngredientsManager = new RecipesIngredientsManager();
        ingredientsManager = new IngredientsManager();
        unitsManager = new UnitsManager();

        recipesTagsManager = new RecipesTagsManager();
        tagsManager = new TagsManager();
    }

    public List<Recipe> getRecipes() {
        return  recipesManager.search(contentResolver,"youtube_url == ? ORDER BY name", new String [] {""});
    }

    public List<Recipe> getRecipesWithVideo(){
        return  recipesManager.search(contentResolver,"youtube_url != ? ORDER BY name", new String [] {""});
    }

    public String getVideoURLForVideo(long id){
        Recipe recipe = recipesManager.getById(contentResolver,id);

        return recipe.youtube_url;
    }

    public Recipe getRecipeById(long id) {
        return recipesManager.getById(contentResolver, id);
    }

    public List<String> getIngredientTextsForRecipe(long id) {
        List<RecipeIngredient> recipesIngredients = recipesIngredientsManager.search(contentResolver, "recipe_id = ?", new String[]{String.valueOf(id)});

        List<String> ingredientsTexts = new ArrayList<>();

        for(RecipeIngredient recipeIngredient:recipesIngredients) {
            Ingredient ingredient = ingredientsManager.getById(contentResolver, recipeIngredient.ingredient_id);
            Unit unit = unitsManager.getById(contentResolver, recipeIngredient.unit_id);
            double amount = recipeIngredient.amount;

            ingredientsTexts.add(getIngredientText(ingredient, unit, amount));
        }

        return ingredientsTexts;
    }

    //TODO: usar join en vez de esta cosa fea de meter mil consultas a la db!!
    public List<Tag> getTagsForRecipe(long id) {
        List<RecipeTag> recipesTags = recipesTagsManager.search(contentResolver, "recipe_id = ?", new String[]{String.valueOf(id)});

        List<Tag> tags = new ArrayList<>();
        for (RecipeTag recipeTag : recipesTags) {
            tags.add(tagsManager.getById(contentResolver, recipeTag.tag_id));
        }

        return tags;
    }

    //TODO usar resources
    private String getIngredientText(Ingredient ingredient, Unit unit, double amount) {
        StringBuilder textBuilder = new StringBuilder();

        if (amount > 0) {
            if((amount == Math.floor(amount)) && !Double.isInfinite(amount)) {
                textBuilder.append(Math.round(amount)).append(" ");
            } else {
                textBuilder.append(amount).append(" ");
            }
        }

        textBuilder.append((amount == 1)? unit.singular_name:unit.plural_name);
        textBuilder.append(" de ").append(ingredient.name.toLowerCase());

        return textBuilder.toString();
    }
}
