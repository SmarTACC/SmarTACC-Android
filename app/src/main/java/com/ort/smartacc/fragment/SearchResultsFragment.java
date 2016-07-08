package com.ort.smartacc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.adapter.ResultAdapter;
import com.ort.smartacc.SearchResult;

import java.util.ArrayList;

public class SearchResultsFragment extends Fragment {

    public final static String ARG_RESULTS = "arg_results";
    private ArrayList<SearchResult> results = null;
    ListView listView;
    TextView t;
    public SearchResultsFragment() {
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        this.results = args.getParcelableArrayList(ARG_RESULTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_search_result, container, false);

        listView = (ListView) view.findViewById(R.id.lstRecipes);
        if(results != null) {
            displayResultList(view);
        }
        return view;
    }

    private void displayResultList(View view){
        //Carga y muestra la lista de resultados
        LayoutInflater inflater;
        if(results.size() == 0){
            ((TextView) view.findViewById(R.id.no_result)).setText("Su búsqueda no ha dado resultados.");
        }
        ResultAdapter resultAdapter = new ResultAdapter(getActivity(), results.toArray(new SearchResult[results.size()]));
        listView.setAdapter(resultAdapter);

        //Seteo el listener para poder escuchar los eventos de click sobre las recetas (resultados)
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
        ((OnRecetaSelectedCallback)getActivity()).onRecetaSelected(selectedRecipe.getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface OnRecetaSelectedCallback{
        void onRecetaSelected(int id);
    }
}
