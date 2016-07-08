package com.ort.smartacc.fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.ort.smartacc.db.SQLiteHelper;
import com.ort.smartacc.net.LoadImageTask;

public class RecetaFragment extends Fragment {

    boolean error404=false;
    public final static String TAG = "recetaFragmentTag";
    public final static String KEY_ID_RECETA="key_id_receta";

    Bitmap image;

    public RecetaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SQLiteDatabase database = new SQLiteHelper(getActivity(), Util.DB_VERSION).getReadableDatabase();

        View view;
        Cursor c = database.query(SQLiteHelper.TABLE_MANAGERS[0].getName(), null, "IDRecetas=" + getArguments().getInt(KEY_ID_RECETA), null, null, null, null);

        if (c.getCount() != 0) {
            error404 = false;
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_receta, container, false);
            c.moveToNext();
            ((TextView) view.findViewById(R.id.receta_titulo)).setText(c.getString(c.getColumnIndex("Nombre")));
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar != null){
                actionBar.setTitle("Receta");//c.getString(c.getColumnIndex("Nombre")));
            }
            ((TextView) view.findViewById(R.id.receta_texto_preparacion)).setText(c.getString(c.getColumnIndex("Texto")));
            //Setear la imagen
            if(image==null) {
                new LoadImageTask(getActivity(), (ImageView) view.findViewById(R.id.receta_foto), new LoadImageTask.OnReadyCallback() {
                    @Override
                    public void saveBitmap(Bitmap bitmap) {
                        image=bitmap;
                    }
                }).execute(Util.SERVER_URL + "celiaquia/" + c.getString(c.getColumnIndex("Imagen")));

            } else{
                ((ImageView)view.findViewById(R.id.receta_foto)).setImageBitmap(image);
            }
            //Poner los ingredientes
            Cursor ingredientes = database.rawQuery("SELECT ingredientes.IDIng, ingredientes.Nombre FROM ingredientes INNER JOIN ingrec ON ingredientes.IDIng=ingrec.IDIng WHERE ingrec.IDRecetas = " + getArguments().getInt(KEY_ID_RECETA) + " ORDER BY ingredientes.Nombre;", null);
            LinearLayout listaIngredientes = (LinearLayout) view.findViewById(R.id.receta_list_ingredientes);
            if(ingredientes.getCount()>0) {
                while (!ingredientes.isLast()) {
                    ingredientes.moveToNext();
                    Cursor datosIngredientes = database.query(SQLiteHelper.TABLE_MANAGERS[4].getName(), null, "IDRecetas=ingrec.IDRecetas AND IDIng = ?", new String[]{ingredientes.getString(ingredientes.getColumnIndex("IDIng"))}, null, null, null);
                    datosIngredientes.moveToNext();
                    if (datosIngredientes.getInt(datosIngredientes.getColumnIndex("Cantidad")) == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(
                                String.format(
                                        getResources().getString(R.string.formato_ingrediente_sin_cantidad),
                                        datosIngredientes.getString(datosIngredientes.getColumnIndex("Unidad")),
                                        ingredientes.getString(ingredientes.getColumnIndex("Nombre"))));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        listaIngredientes.addView(textView);
                    } else {
                        TextView textView = new TextView(getActivity());
                        textView.setText(
                                String.format(
                                        getResources().getString(R.string.formato_ingrediente_con_cantidad),
                                        datosIngredientes.getFloat(datosIngredientes.getColumnIndex("Cantidad")),
                                        datosIngredientes.getString(datosIngredientes.getColumnIndex("Unidad")),
                                        ingredientes.getString(ingredientes.getColumnIndex("Nombre"))));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        listaIngredientes.addView(textView);
                    }
                    datosIngredientes.close();
                }
            }
            ingredientes.close();
            //Poner los tags
            Cursor tags = database.rawQuery("SELECT * FROM tags INNER JOIN tagrec ON tags.IDTag=tagrec.IDTag WHERE tagrec.IDReceta = " + getArguments().getInt(KEY_ID_RECETA) + " ORDER BY tags.Nombre;", null);
            LinearLayout listaTags = (LinearLayout) view.findViewById(R.id.receta_list_categorias);
            if(tags.getCount()>0) {
                while (!tags.isLast()) {
                    tags.moveToNext();
                    TextView textView = new TextView(getActivity());
                    textView.setText("\u2022 " + tags.getString(tags.getColumnIndex("Nombre")));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    listaTags.addView(textView);
                }
            }
            tags.close();
        } else {
            error404 = true;
            view = inflater.inflate(R.layout.error404, container, false);
        }
        c.close();
        database.close();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
