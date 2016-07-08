package com.ort.smartacc.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.adapter.RecetarioAdapter;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.Util;
import com.ort.smartacc.activity.RecetaActivity;
import com.ort.smartacc.db.SQLiteHelper;

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

        String query = "SELECT IDRecetas, COUNT(*) AS cant, Nombre, Imagen FROM recetas GROUP BY IDRecetas ORDER BY Nombre";

        //Hago el query:

        //Hago un request al servidor para conseguir la version de la DB

        SQLiteDatabase db = new SQLiteHelper(getActivity(), Util.DB_VERSION).getReadableDatabase();

        ArrayList<SearchResult> results = new ArrayList<>();
        Cursor queryResult = null;
        if(db != null) {
            queryResult = db.rawQuery(query, new String[0]);
        }
        if (queryResult != null) {
            if(queryResult.moveToFirst())
            {
                do{
                    String idRecetaActual = queryResult.getString(queryResult.getColumnIndex("IDRecetas"));
                    String nombreRecetaActual = queryResult.getString(queryResult.getColumnIndex("Nombre"));
                    String imagenRecetaActual = queryResult.getString(queryResult.getColumnIndex("Imagen"));
                    results.add(new SearchResult(Integer.parseInt(idRecetaActual), nombreRecetaActual, imagenRecetaActual));
                } while(queryResult.moveToNext());
            }
            queryResult.close();
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
        args.putInt(RecetaFragment.KEY_ID_RECETA, selectedRecipe.getId());
        intent.putExtra(RecetaActivity.ARG_EXTRAS, args);
        startActivity(intent);
    }
}
