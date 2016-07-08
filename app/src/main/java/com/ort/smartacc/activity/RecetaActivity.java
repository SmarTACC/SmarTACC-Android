package com.ort.smartacc.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.ort.smartacc.R;
import com.ort.smartacc.fragment.RecetaFragment;

public class RecetaActivity extends AppCompatActivity {
    public final static String ARG_EXTRAS = "arg_extras";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if(savedInstanceState==null) {
            RecetaFragment fragment = new RecetaFragment();
            fragment.setArguments(getIntent().getBundleExtra(ARG_EXTRAS));
            getSupportFragmentManager().beginTransaction().add(R.id.activity_receta_container, fragment).commit();
        }
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
