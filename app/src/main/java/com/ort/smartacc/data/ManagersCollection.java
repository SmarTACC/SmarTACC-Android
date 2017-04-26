package com.ort.smartacc.data;

import com.ort.smartacc.data.managers.CategoriesManager;
import com.ort.smartacc.data.managers.IngredientsManager;
import com.ort.smartacc.data.managers.PlacesManager;
import com.ort.smartacc.data.managers.RecipesIngredientsManager;
import com.ort.smartacc.data.managers.RecipesManager;
import com.ort.smartacc.data.managers.RecipesTagsManager;
import com.ort.smartacc.data.managers.TableManager;
import com.ort.smartacc.data.managers.TagsManager;
import com.ort.smartacc.data.managers.UnitsManager;

public class ManagersCollection {
    private TableManager[] tableManagers = {
            new RecipesManager(),
            new CategoriesManager(),
            new IngredientsManager(),
            new PlacesManager(),
            new RecipesIngredientsManager(),
            new RecipesTagsManager(),
            new TagsManager(),
            new UnitsManager()
    };

    public TableManager[] getTableManagers() {
        return tableManagers;
    }

    public TableManager getTableManager(int index) {
        return tableManagers[index];
    }

    public int getSize() {
        return tableManagers.length;
    }
}
