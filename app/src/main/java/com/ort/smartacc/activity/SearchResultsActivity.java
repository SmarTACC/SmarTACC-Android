package com.ort.smartacc.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.ort.smartacc.R;
import com.ort.smartacc.activity.RecetaActivity;
import com.ort.smartacc.fragment.RecetaFragment;
import com.ort.smartacc.fragment.SearchResultsFragment;

public class SearchResultsActivity extends AppCompatActivity
        implements SearchResultsFragment.OnRecetaSelectedCallback{
    public final static String ARG_EXTRA_BUNDLE = "arg_extra_bundle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SearchResultsFragment fragment = new SearchResultsFragment();
        fragment.setArguments(getIntent().getBundleExtra(ARG_EXTRA_BUNDLE));
        getSupportFragmentManager().beginTransaction().add(R.id.activity_search_result_container, fragment).commit();
    }

    @Override
    public void onRecetaSelected(int id) {
        Intent intent = new Intent(this, RecetaActivity.class);
        Bundle args = new Bundle();
        args.putInt(RecetaFragment.KEY_ID_RECETA, id);
        intent.putExtra(RecetaActivity.ARG_EXTRAS, args);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
