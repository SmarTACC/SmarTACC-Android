package com.ort.smartacc.fragment;

/**
 * Created by sebitokazu on 27/4/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ort.smartacc.R;
import com.ort.smartacc.data.RecipesAgent;
import com.ort.smartacc.data.model.Recipe;


public class VideoFragment extends Fragment {
    public final static  String KEY_ID_RECETA = "key_id_receta";
    public static String VIDEO_URL;


    public  VideoFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;
        final RecipesAgent recipesAgent = new RecipesAgent(getContext());
        Recipe recipe = recipesAgent.getRecipeById(getArguments().getLong(KEY_ID_RECETA));

        if(recipe != null) {

            rootView = inflater.inflate(R.layout.fragment_video, container, false);
            ((TextView) rootView.findViewById(R.id.receta_video_titulo)).setText(recipe.name);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar != null){
                actionBar.setTitle("Videos Listo");//c.getString(c.getColumnIndex("Nombre")));
            }

            YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();

            //TODO: cambiarle el nombre a google_maps_key por algo más general (google_api_key por ejemplo)
            youTubePlayerFragment.initialize(getResources().getString(R.string.google_maps_key), new OnInitializedListener() {

                @Override
                public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
                    if (!wasRestored) {
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        VIDEO_URL = recipesAgent.getVideoURLForVideo(getArguments().getLong(KEY_ID_RECETA));
                        player.loadVideo(VIDEO_URL);
                        player.play();
                    }
                }

                @Override
                public void onInitializationFailure(Provider provider, YouTubeInitializationResult error) {
                    // YouTube error
                    String errorMessage = error.toString();
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("errorMessage:", errorMessage);
                }
            });
        }else{
            rootView = inflater.inflate(R.layout.error404, container, false);
        }

        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
