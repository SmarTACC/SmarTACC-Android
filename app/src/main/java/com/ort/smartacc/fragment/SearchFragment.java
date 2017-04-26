package com.ort.smartacc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ort.smartacc.MultiSpinner;
import com.ort.smartacc.R;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.data.SearchAgent;
import com.ort.smartacc.data.model.Ingredient;
import com.ort.smartacc.data.model.Recipe;
import com.ort.smartacc.data.model.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends android.support.v4.app.Fragment{

    ArrayList<String> selectedTags = new ArrayList<>();
    ArrayList<String> selectedIngredients = new ArrayList<>();

    Map<String, Long> tagsIds = new HashMap<>();
    Map<String, Long> ingredientsIds = new HashMap<>();

    SearchAgent searchAgent;

    EditText txtName;

    public SearchFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        txtName = (EditText) view.findViewById(R.id.txtRecipeName);

        searchAgent = new SearchAgent(getContext());

        Button btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearch_Click();
            }
        });
        fillSpinners((MultiSpinner) view.findViewById(R.id.sprCategories), (MultiSpinner) view.findViewById(R.id.sprIngredients));
        return view;
    }

    public void btnSearch_Click(){
        if(!txtName.getText().toString().equals("") || selectedIngredients.size() != 0 || selectedTags.size() != 0) {
            List<Recipe> recipes = searchAgent.searchRecipes(txtName.getText().toString(), namesToIds(ingredientsIds, selectedIngredients), namesToIds(tagsIds, selectedTags));

            ArrayList<SearchResult> results = new ArrayList<>();

            for (Recipe recipe : recipes) {
                results.add(new SearchResult(recipe.id, recipe.name));
            }

            ((OnSearchInteractionCallback)getActivity()).showResultsFragment(results);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Por favor, introduzca un criterio de búsqueda", Toast.LENGTH_LONG).show();
        }
    }

    private void fillSpinners(MultiSpinner sprCategories, MultiSpinner sprIngredients){
        //Lleno los dos MultiSpinners con sus respectivos datos

        List<Ingredient> ingredients = searchAgent.getIngredients();
        List<Tag> tags = searchAgent.getTags();

        List<String> ingredientsNames = new ArrayList<>();
        for(Ingredient ingredient: ingredients) {
            ingredientsNames.add(ingredient.name);
            ingredientsIds.put(ingredient.name, ingredient.id);
        }

        List<String> tagsNames = new ArrayList<>();
        for(Tag tag: tags) {
            tagsNames.add(tag.name);
            tagsIds.put(tag.name, tag.id);
        }

        //Cargo los datos en los spinners
        sprCategories.setItems(tagsNames, "Todas las categorias", "Seleccionar", false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<String> items, boolean[] selected) {
                //Consigo todos las categiruas seleccionados y los agrego, si todavía no están, a la lista de categorias seleccionados
                marcarSelectos(selectedTags, items, selected);
            }
        });

        sprIngredients.setItems(ingredientsNames, "Todos los ingredientes", "Seleccionar", false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<String> items, boolean[] selected) {
                //Consigo todos los ingredientes seleccionados y los agrego, si todavía no están, a la lista de ingredientes seleccionados
                marcarSelectos(selectedIngredients, items, selected);
            }
        });
    }

    /**
     * Funcion para generalizar la actualizacion de selectedIngredients y selected categories
     * @param listSelectos selectedIngredients/selectedTags
     * @param listTotal lista de todos los ingredientes/categorias
     * @param selectos array indicando cuales de los ingredientes/categorias fueron seleccionados
     */
    void marcarSelectos(List<String> listSelectos, List<String> listTotal, boolean[] selectos){
        listSelectos.clear();
        for (int i = 0; i<selectos.length; i++)
        {
            if(selectos[i]) {
                listSelectos.add(listTotal.get(i));
            }
        }
    }

    private List<Long> namesToIds(Map<String, Long> map, List<String> names) {
        List<Long> ids = new ArrayList<>();

        for(String name:names) {
            ids.add(map.get(name));
        }

        return ids;
    }

    public interface OnSearchInteractionCallback{
        void showResultsFragment(ArrayList<SearchResult> results);
    }
}
