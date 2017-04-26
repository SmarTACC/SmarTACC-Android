package com.ort.smartacc.data;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;

import com.ort.smartacc.data.managers.CategoriesManager;
import com.ort.smartacc.data.managers.PlacesManager;
import com.ort.smartacc.data.model.Category;
import com.ort.smartacc.data.model.Place;

import java.util.List;

public class PlacesAgent {

    ContentResolver contentResolver;
    PlacesManager placesManager;
    CategoriesManager categoriesManager;

    public PlacesAgent(Context context) {
        contentResolver = context.getContentResolver();

        placesManager = new PlacesManager();
        categoriesManager = new CategoriesManager();
    }

    public List<Category> getCategories() {
        return categoriesManager.getAll(contentResolver, "name");
    }

    public List<Place> getPlacesByCategories(List<Long> categoriesIds) {
        return placesManager.search(contentResolver, "category_id IN (" + TextUtils.join(",", categoriesIds) + ")", null);
    }
}
