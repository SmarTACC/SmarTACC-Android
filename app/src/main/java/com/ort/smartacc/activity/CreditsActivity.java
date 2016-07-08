package com.ort.smartacc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ort.smartacc.R;

public class CreditsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Button btnSugerencias = (Button) findViewById(R.id.btnSugerencias);
        if(btnSugerencias != null) {
            btnSugerencias.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:smartacc@ort.edu.ar"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sugerencias para mejorar"); // if you want extra
                startActivity(intent);*/
                    startActivity(new Intent(CreditsActivity.this, SugerenciasActivity.class));
                }
            });
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
