package com.ort.smartacc.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.Util;
import com.ort.smartacc.data.RecipesAgent;
import com.ort.smartacc.data.model.Recipe;
import com.ort.smartacc.data.model.Tag;
import com.ort.smartacc.net.LoadImageTask;

import java.util.List;

public class RecetaFragment extends Fragment {

    public final static String TAG = "recetaFragmentTag";
    public final static String KEY_ID_RECETA="key_id_receta";

    Bitmap image;

    public RecetaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        RecipesAgent recipesAgent = new RecipesAgent(getContext());
        Recipe recipe = recipesAgent.getRecipeById(getArguments().getLong(KEY_ID_RECETA));

        if (recipe != null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_receta, container, false);
            ((TextView) view.findViewById(R.id.receta_titulo)).setText(recipe.name);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar != null){
                actionBar.setTitle("Receta");//c.getString(c.getColumnIndex("Nombre")));
            }

            ((TextView) view.findViewById(R.id.receta_texto_preparacion)).setText(recipe.description);
            //Setear la imagen
            if(image == null) {
                new LoadImageTask(getActivity(), (ImageView) view.findViewById(R.id.receta_foto), new LoadImageTask.OnReadyCallback() {
                    @Override
                    public void saveBitmap(Bitmap bitmap) {
                        image=bitmap;
                    }
                }).execute(Util.IMAGES_URL + recipe.id + ".jpg");

            } else{
                ((ImageView)view.findViewById(R.id.receta_foto)).setImageBitmap(image);
            }

            //Poner los ingredientes
            List<String> ingredientsTexts = recipesAgent.getIngredientTextsForRecipe(recipe.id);
            LinearLayout listaIngredientes = (LinearLayout) view.findViewById(R.id.receta_list_ingredientes);
            for(String ingredientText: ingredientsTexts) {
                TextView textView = new TextView(getActivity());
                textView.setText("\u2022 " + ingredientText);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                listaIngredientes.addView(textView);
            }
            //Poner los tags
            List<Tag> tags = recipesAgent.getTagsForRecipe(recipe.id);
            LinearLayout listaTags = (LinearLayout) view.findViewById(R.id.receta_list_categorias);
            for(Tag tag: tags) {
                TextView textView = new TextView(getActivity());
                textView.setText("\u2022 " + tag.name);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                listaTags.addView(textView);
            }
        } else {
            view = inflater.inflate(R.layout.error404, container, false);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
