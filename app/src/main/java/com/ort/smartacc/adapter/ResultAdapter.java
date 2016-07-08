package com.ort.smartacc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.SearchResult;
import com.ort.smartacc.Util;
import com.squareup.picasso.Picasso;


public class ResultAdapter extends ArrayAdapter<SearchResult> {

    private SearchResult[] results = null;
    private Context context;

    public ResultAdapter(Context context, SearchResult[] results) {
        super(context, R.layout.item_search_single, results);
        this.results = results;
        this.context = context;
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
        Picasso.with(this.context).load(Util.SERVER_URL + "celiaquia/" + result.getImage()).fit().into(holder.imgRecipeImage);
        holder.imgRecipeImage.setContentDescription(result.getName());

        return convertView;
    }
}
