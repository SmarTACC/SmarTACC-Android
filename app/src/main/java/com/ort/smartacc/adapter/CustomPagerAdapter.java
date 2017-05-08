package com.ort.smartacc.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ort.smartacc.fragment.ErrorFragment;
import com.ort.smartacc.fragment.MapFragment;
import com.ort.smartacc.fragment.RecetarioFragment;
import com.ort.smartacc.fragment.SearchFragment;
import com.ort.smartacc.fragment.RecetarioVideoListoFragment;
import com.ort.smartacc.fragment.VideoFragment;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {
    //Los tabs van a ser: videos, recetas, mapa, busqueda, top
    private final String[] titles = {"VIDEOS LISTO", "RECETAS", "MAPA", "BUSQUEDA", /*"TOP 10"*/};

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        switch (titles[position]){
            case "VIDEOS LISTO":
                fragment = new RecetarioVideoListoFragment();
                break;
            case "RECETAS":
                fragment = new RecetarioFragment();
                break;
            case "MAPA":
                fragment = new MapFragment();
                break;
            case "BUSQUEDA":
                fragment = new SearchFragment();
                break;
            case "TOP 10":
                fragment = new ErrorFragment();
                args.putString(ErrorFragment.ARG_DESCRIPCION, "Proximamente: TOP 10 :)");
                fragment.setArguments(args);
                break;
            default:
                //Mostrar error inesperado
                fragment = new ErrorFragment();
                args.putString(ErrorFragment.ARG_DESCRIPCION, "Disculpe las molestias, por favor reinicie la aplicacion.");
                fragment.setArguments(args);
        }
        return fragment;
    }
}