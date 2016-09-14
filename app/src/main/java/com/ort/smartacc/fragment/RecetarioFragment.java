package com.ort.smartacc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.activity.RecetaActivity;
import com.ort.smartacc.adapter.RecetarioAdapter;
import com.ort.smartacc.data.RecipesAgent;
import com.ort.smartacc.data.model.Recipe;

import java.util.ArrayList;

public class RecetarioFragment extends android.support.v4.app.Fragment{

    View view;

    ListView listView;

    public RecetarioFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recetario, container, false);
        listView = (ListView) view.findViewById(R.id.lstRecetario);
        llenarRecetario();
        return view;
    }

    public void llenarRecetario(){
        ArrayList<SearchResult> results = new ArrayList<>();
        RecipesAgent recipesAgent = new RecipesAgent(getContext());

        for(Recipe recipe:recipesAgent.getRecipes()) {
            //TODO: cmon.. esto es feo
            results.add(new SearchResult((int) recipe.id, recipe.name));
        }

        //Finalmente, cargo el ListView
        RecetarioAdapter recetarioAdapter = new RecetarioAdapter(getActivity(), results.toArray(new SearchResult[results.size()]), (TextView) view.findViewById(R.id.lblCurrentSection));
        listView.setAdapter(recetarioAdapter);
        listView.setOnScrollListener(recetarioAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });
    }

    private void onListItemClick(AdapterView<?> parent, View view, int position, long id){
        SearchResult selectedRecipe = (SearchResult) parent.getAdapter().getItem(position);
        //Toast.makeText(this.getActivity(), "Selected: " + selectedRecipe.getId(), Toast.LENGTH_SHORT).show(); //Para probar que funcione el evento
        Intent intent = new Intent(getActivity(), RecetaActivity.class);
        Bundle args = new Bundle();
        args.putLong(RecetaFragment.KEY_ID_RECETA, selectedRecipe.getId());
        intent.putExtra(RecetaActivity.ARG_EXTRAS, args);
        startActivity(intent);
    }
}
