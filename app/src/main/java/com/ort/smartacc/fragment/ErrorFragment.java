package com.ort.smartacc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ort.smartacc.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {

    public final static String ARG_DESCRIPCION = "arg_descripcion";
    private String descripcion;

    public ErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if(args.containsKey(ARG_DESCRIPCION)){
            descripcion = args.getString(ARG_DESCRIPCION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_error, container, false);

        if(savedInstanceState!=null)
            descripcion = savedInstanceState.getString(ARG_DESCRIPCION);

        TextView txtDescripcion = (TextView) view.findViewById(R.id.txt_error_descripcion);
        txtDescripcion.setText(descripcion);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_DESCRIPCION, descripcion);
    }
}
