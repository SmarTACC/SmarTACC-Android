package com.ort.smartacc.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.ort.smartacc.data.PlacesAgent;
import com.ort.smartacc.data.model.Category;
import com.ort.smartacc.data.model.Place;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    GoogleMap googleMap;
    View pgrMap;

    PlacesAgent placesAgent;

    Map<Long, String> categoriesNames;
    Map<String, Long> categoriesIds;
    Map<String, Boolean> selectedCategories;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        pgrMap = view.findViewById(R.id.pgrMap);

        placesAgent = new PlacesAgent(getContext());

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
        List<Category> categories = placesAgent.getCategories();
        List<String> categoriesNamesList = new ArrayList<>();

        categoriesNames = new HashMap<>();
        categoriesIds = new HashMap<>();
        selectedCategories = new HashMap<>();

        for(Category category:categories) {
            categoriesNamesList.add(category.name);
            categoriesNames.put(category.id, category.name);
            categoriesIds.put(category.name, category.id);
            selectedCategories.put(category.name, true);
        }

        //TODO arreglar multispinner para que funcione con tables
        spinner.setItems(categoriesNamesList, "Todos las categorias", "Filtrar por categoria", true, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<String> items, boolean[] selected) {
                for (int i = 0; i < selected.length; i++) {
                    selectedCategories.put(items.get(i), selected[i]);
                }
                fillMap();
            }
        });
    }

    void fillMap(){
        googleMap.clear();
        new MapFillAsyncTask().execute();
    }

    class MapFillAsyncTask extends AsyncTask<Void, Void, List<Place>> {

        @Override
        protected void onPreExecute() {
            pgrMap.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Place> doInBackground(Void... params) {
            List<Long> selectedCategoriesIds = new ArrayList<>();

            for(String key:selectedCategories.keySet()) {
                if(selectedCategories.get(key)) {
                    selectedCategoriesIds.add(categoriesIds.get(key));
                }
            }

            return placesAgent.getPlacesByCategories(selectedCategoriesIds);
        }

        @Override
        protected void onPostExecute(List<Place> places) {

            for(Place place:places) {
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(place.lat, place.lon))
                        .title(place.name)
                        .snippet(place.address + "\n" + place.description);

                try {
                    // TODO que las imagenes tengan de nombre id en vez de nombres, asi nos evitamos "categoriesNames"
                    int id = getContext().getResources().getIdentifier(
                            Normalizer.normalize(
                                    categoriesNames.get(place.category_id).toLowerCase(),
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
