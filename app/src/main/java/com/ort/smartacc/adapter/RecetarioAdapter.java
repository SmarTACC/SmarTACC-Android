package com.ort.smartacc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RecetarioAdapter extends ArrayAdapter<SearchResult> implements SectionIndexer, AbsListView.OnScrollListener{

    private SearchResult[] results = null;
    private Context context;
    private HashMap<String,Integer> alphaIndexer;
    private String[] sections;

    private TextView lblCurrentSection;

    public RecetarioAdapter(Context context, SearchResult[] results, TextView lblCurrentSection) {
        super(context, R.layout.item_search_single, results);
        this.lblCurrentSection = lblCurrentSection;
        this.results = results;
        this.context = context;
        alphaIndexer = new HashMap<>();
        int size = results.length;

        //Cargo alphaIndexer para que contenga todas las iniciales de los nombres de las recetas
        //(si ya tiene una letra, no la agrega de nuevo)
        ArrayList<String> tempSections = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String nombreReceta = results[i].getName();
            String inicialReceta = nombreReceta.substring(0, 1);
            inicialReceta = inicialReceta.toUpperCase();
            if(!tempSections.contains(inicialReceta)) {
                //Se suma esto porque los headers cuentan como un elemento. Esto quiere decir que
                //si el primer elemento con una letra está en la posición 1, el header estará en
                //dicha posición.
                //Ahora, si está en la posición 3, el header estará en la 4, pues ya añadí un header
                //anteriormente. Es decir, todos los números se corren la cantidad de letras que
                //haya antes de la que se está tratando.
                //alphaIndexer.put(inicialReceta, i + tempSections.size());
                alphaIndexer.put(inicialReceta, i);
                tempSections.add(inicialReceta);
            }
        }

        sections = new String[tempSections.size()];
        tempSections.toArray(sections);
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return alphaIndexer.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Toast.makeText(context, "Scrolled!", Toast.LENGTH_SHORT).show();
        String currentSection = "A";
        int currentSectionIndex = 0;
        for (Map.Entry<String, Integer> e : alphaIndexer.entrySet())
        {
            if(currentSectionIndex < e.getValue() && e.getValue() <= firstVisibleItem) {
                currentSection = e.getKey();
                currentSectionIndex = e.getValue();
            }
        }
        lblCurrentSection.setText(currentSection);
    }

    static class ResultHolder
    {
        ImageView imgRecipeImage;
        TextView lblRecipeName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Cargo el nombre de cada receta y su imagen en cada instancia del layout item_search_single
        //Básicamente, cargo los resultados de la búsqueda.

        ResultHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_search_single, parent, false);

            holder = new ResultHolder();
            holder.imgRecipeImage = (ImageView)convertView.findViewById(R.id.imgRecipeImage);
            holder.lblRecipeName = (TextView)convertView.findViewById(R.id.lblRecipeName);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ResultHolder)convertView.getTag();
        }

        //Cargo el TextView del nombre y el ImageView (la foto)
        SearchResult result = results[position];
        holder.lblRecipeName.setText(result.getName());
        holder.lblRecipeName.setGravity(Gravity.CENTER_VERTICAL);
        Picasso.with(this.context).load(Util.IMAGES_URL + result.getId() + ".jpg").fit().into(holder.imgRecipeImage);
        holder.imgRecipeImage.setContentDescription(result.getName());

        return convertView;
    }
}
