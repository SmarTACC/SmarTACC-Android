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
import com.ort.smartacc.activity.VideoActivity;
import com.ort.smartacc.adapter.RecetarioAdapter;
import com.ort.smartacc.adapter.ResultAdapter;
import com.ort.smartacc.data.RecipesAgent;
import com.ort.smartacc.data.model.Recipe;

import java.util.ArrayList;

public class RecetarioVideoListoFragment extends android.support.v4.app.Fragment{

    View view;

    ListView listView;

    public RecetarioVideoListoFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recetariovideolisto, container, false);
        listView = (ListView) view.findViewById(R.id.lstRecetarioVideoListo);
        llenarRecetario();
        return view;
    }

    public void llenarRecetario(){
        ArrayList<SearchResult> results = new ArrayList<>();
        RecipesAgent recipesAgent = new RecipesAgent(getContext());

        for(Recipe recipe:recipesAgent.getRecipesWithVideo()) {
            //TODO: cmon.. esto es feo
            results.add(new SearchResult((int) recipe.id, recipe.name));
        }

        //Finalmente, cargo el ListView
        //TODO: unificar resultAdapter y recetarioAdapter?
        ResultAdapter resultAdapter = new ResultAdapter(getActivity(), results.toArray(new SearchResult[results.size()]));
        listView.setAdapter(resultAdapter);
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
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        Bundle args = new Bundle();
        args.putLong(VideoFragment.KEY_ID_RECETA, selectedRecipe.getId());
        intent.putExtra(VideoActivity.ARG_EXTRAS, args);
        startActivity(intent);
    }
}
