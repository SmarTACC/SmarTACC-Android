package com.ort.smartacc.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ort.smartacc.MultiSpinner;
import com.ort.smartacc.R;
import com.ort.smartacc.Util;
import com.ort.smartacc.db.SQLiteHelper;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    List<String> selectedCategories = new ArrayList<>();
    GoogleMap googleMap;
    View pgrMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        pgrMap = view.findViewById(R.id.pgrMap);
        fillSpinner((MultiSpinner) view.findViewById(R.id.sprMap));

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapFragment.this.googleMap = googleMap;
                LatLng buenosAires = new LatLng(-34.5573429, -58.4594648);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 12.0f));

                googleMap.setInfoWindowAdapter(new CustomInfoWindow(MapFragment.this.getActivity()));
                fillMap();
            }
        });
        return view;
    }

    void fillSpinner(MultiSpinner spinner){
        List<String> categories = new ArrayList<>();

        SQLiteDatabase db = new SQLiteHelper(getActivity(), Util.DB_VERSION).getReadableDatabase();

        //Hago el query y lleno la lista de categorias
        Cursor c = db.rawQuery("SELECT IDCategory,name FROM categories ORDER BY name", null);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            categories.add(c.getString(1));
        }
        c.close();

        spinner.setItems(categories, "Todos las categorias", "Filtrar por categoria", true, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<String> items, boolean[] selected) {
                marcarSelectos(selectedCategories, items, selected);
                fillMap();
            }
        });
        selectedCategories.addAll(categories);
    }

    void marcarSelectos(List<String> listSelectos, List<String> listTotal, boolean[] selectos){
        listSelectos.clear();
        for (int i = 0; i<selectos.length; i++)
        {
            if(selectos[i]) {
                listSelectos.add(listTotal.get(i));
            }
        }
    }

    void fillMap(){
        googleMap.clear();
        new MapFillAsyncTask().execute();
    }

    class MapFillAsyncTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            pgrMap.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            SQLiteDatabase db = new SQLiteHelper(getActivity(), Util.DB_VERSION).getReadableDatabase();
            StringBuilder queryCategories = new StringBuilder();
            for(int i = 0; i < selectedCategories.size(); i++){
                if(i!=0){
                    queryCategories.append(",");
                }
                queryCategories.append("'").append(selectedCategories.get(i)).append("'");
            }

            return db.rawQuery("SELECT lat, lon, lugares.name AS name, categories.name AS catName, address, description FROM lugares INNER JOIN categories ON lugares.IDCategory=categories.IDCategory WHERE categories.name IN("+queryCategories.toString()+")", null);
        }
        @Override
        protected void onPostExecute(Cursor c) {
            while(c.moveToNext()){

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(c.getDouble(c.getColumnIndex("lat")), c.getDouble(c.getColumnIndex("lon"))))
                        .title(c.getString(c.getColumnIndex("name")))
                        .snippet(c.getString(c.getColumnIndex("address")) + "\n" + c.getString(c.getColumnIndex("description")));

                try {
                    int id = getContext().getResources().getIdentifier(
                            Normalizer.normalize(
                                    c.getString(c.getColumnIndex("catName")).toLowerCase(),
                                    Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""),
                            "drawable",
                            getContext().getPackageName());

                    if(id!=0)
                        marker.icon(BitmapDescriptorFactory.fromResource(id));
                } catch (Resources.NotFoundException nfe){
                    nfe.printStackTrace();
                }
                if(googleMap!=null) {
                    googleMap.addMarker(marker);
                }
            }

            pgrMap.setVisibility(View.GONE);
            c.close();
        }
    }

    class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
        Activity activity;
        CustomInfoWindow(Activity activity) {
            this.activity=activity;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View infoWindow=activity.getLayoutInflater().inflate(R.layout.info_window, null);

            TextView name=(TextView)infoWindow.findViewById(R.id.info_window_name);
            name.setText(marker.getTitle());
            String[] snippet = marker.getSnippet().split("\n");
            TextView address=(TextView)infoWindow.findViewById(R.id.info_window_address);
            address.setText(String.format(activity.getResources().getString(R.string.mapa_info_direccion), snippet[0]));
            if(snippet.length>1) {
                TextView description = (TextView) infoWindow.findViewById(R.id.info_window_description);
                description.setText(String.format(activity.getResources().getString(R.string.mapa_info_descipcion), snippet[1]));
            }
            return(infoWindow);
        }
    }
}
