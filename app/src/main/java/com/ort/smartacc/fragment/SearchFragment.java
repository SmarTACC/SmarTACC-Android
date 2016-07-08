package com.ort.smartacc.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ort.smartacc.MultiSpinner;
import com.ort.smartacc.R;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.Util;
import com.ort.smartacc.db.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends android.support.v4.app.Fragment{

    ArrayList<String> selectedCategories = new ArrayList<>();
    ArrayList<String> selectedIngredients = new ArrayList<>();

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
        if(!txtName.getText().toString().equals("") || selectedIngredients.size() != 0 || selectedCategories.size() != 0) {
            StringBuilder query = new StringBuilder("SELECT recetas.IDRecetas, COUNT(*) AS cant, recetas.Nombre, recetas.Imagen FROM recetas ");

            int cantIng = 0;
            int cantCat = 0;
            StringBuilder queryIngredients = new StringBuilder();
            StringBuilder queryCategories = new StringBuilder();

            //Si el usuario seleccionó algún ingrediente, creo la condición por ingrediente
            if (selectedIngredients.size() != 0) {
                for (String ingredient : selectedIngredients) {
                    if (cantIng == 0) {
                        queryIngredients.append("ingredientes.Nombre IN (");
                    } else {
                        queryIngredients.append(",");
                    }
                    //queryIngredients += ingredients.get(ingredient);
                    queryIngredients.append("'").append(ingredient).append("'");
                    cantIng++;
                }
                //agrego conexion con la tabla de ingredientes
                queryIngredients.append(") ");
                query.append("INNER JOIN ingrec ON recetas.IDRecetas=ingrec.IDRecetas INNER JOIN ingredientes ON ingrec.IDIng = ingredientes.IDIng ");
            }

            //Lo mismo que para los ingredientes; Si el usuario seleccionó alguna categoría, creo la condición
            if (selectedCategories.size() != 0) {
                for (String category : selectedCategories) {
                    if (cantCat == 0) {
                        queryCategories.append("tags.Nombre IN (");
                    } else {
                        queryCategories.append(",");
                    }
                    //queryCategories += categories.get(category);
                    queryCategories.append("'").append(category).append("'");
                    cantCat++;
                }
                queryCategories.append(") ");
                query.append("INNER JOIN tagrec ON recetas.IDRecetas=tagrec.IDReceta INNER JOIN tags ON tagrec.IDTag = tags.IDTag ");
            }

            //where guarda si ya puse la palabra WHERE en la query o tengo que ponerla (si no se selecciona ningun filtro no se usa WHERE)
            boolean where = false;
            if (!TextUtils.isEmpty(txtName.getText().toString())) { //Agrega condicion por nombre
                query.append("WHERE recetas.Nombre LIKE '%").append(txtName.getText()).append("%' ");
                where = true;
            }
            if (cantIng > 0) { //Agrega condicion por ingrediente
                if (!where) {
                    query.append(" WHERE ");
                    where = true;
                } else {
                    query.append("AND ");
                }

                query.append(queryIngredients);
            }
            if (cantCat > 0) { //Agrega condicion por tag
                if (!where) {
                    query.append("WHERE ");
                }
                else {
                    query.append("AND ");
                }
                query.append(queryCategories);
            }
            query.append("GROUP BY recetas.IDRecetas ");

            if (cantCat > 0 && cantIng > 0) {
                //Si hay filtro por tag Y por ingrediente, va a pasar que si hay T tags e I ingredientes
                //Con cada tag se deben encontrar I filas que cumplan las condiciones
                //Sino, esa receta le falta un ingrediente
                query.append("HAVING cant = ").append(cantIng * cantCat).append(" ");
            } else {
                if (cantCat > 0 || cantIng > 0) {
                    query.append("HAVING cant = ").append(cantIng + cantCat).append(" ");   //Como se que uno es 0 y la cantidad debe ser el contador que no es 0,
                    //Sumandolos me va a dar el valor del contador que no vale 0
                }
            }
            query.append("ORDER BY recetas.Nombre");

            //Hago el query:
            SQLiteDatabase db = new SQLiteHelper(getActivity(), Util.DB_VERSION).getReadableDatabase();

            ArrayList<SearchResult> results = new ArrayList<>();
            Cursor queryResult = null;
            if(db != null) {
                String finalQuery = query.toString();
                queryResult = db.rawQuery(finalQuery, null);
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
            ((OnSearchInteractionCallback)getActivity()).showResultsFragment(results);
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Por favor, introduzca un criterio de búsqueda", Toast.LENGTH_LONG).show();
        }
    }

    private void fillSpinners(MultiSpinner sprCategories, MultiSpinner sprIngredients){
        //Lleno los dos MultiSpinners con sus respectivos datos

        List<String> categories = new ArrayList<>();
        List<String> ingredients = new ArrayList<>();

        SQLiteDatabase db = new SQLiteHelper(getActivity(), Util.DB_VERSION).getReadableDatabase();

        //Hago el query y lleno la lista de categorias
        Cursor c = db.rawQuery("SELECT IDTag,Nombre FROM tags ORDER BY Nombre", null);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            categories.add(c.getString(1));
        }
        c.close();

        //Hago el query y lleno la lista de ingredientes
        c = db.rawQuery("SELECT IDIng, Nombre FROM ingredientes ORDER BY Nombre", null);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ingredients.add(c.getString(1));
        }
        c.close();


        //Cargo los datos en los spinners
        sprCategories.setItems(categories, "Todas las categorias", "Seleccionar", false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<String> items, boolean[] selected) {
                //Consigo todos las categiruas seleccionados y los agrego, si todavía no están, a la lista de categorias seleccionados
                marcarSelectos(selectedCategories, items, selected);
            }
        });

        sprIngredients.setItems(ingredients, "Todos los ingredientes", "Seleccionar", false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<String> items, boolean[] selected) {
                //Consigo todos los ingredientes seleccionados y los agrego, si todavía no están, a la lista de ingredientes seleccionados
                marcarSelectos(selectedIngredients, items, selected);
            }
        });
    }

    /**
     * Funcion para generalizar la actualizacion de selectedIngredients y selected categories
     * @param listSelectos selectedIngredients/selectedCategories
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

    public interface OnSearchInteractionCallback{
        void showResultsFragment(ArrayList<SearchResult> results);
    }
}
