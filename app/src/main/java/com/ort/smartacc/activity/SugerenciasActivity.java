package com.ort.smartacc.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ort.smartacc.R;
import com.ort.smartacc.Util;
import com.ort.smartacc.net.HttpResponse;
import com.ort.smartacc.net.RequestTask;

public class SugerenciasActivity extends AppCompatActivity {

    TextView txtCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtCountdown = (TextView) findViewById(R.id.txtCountdown);
        final EditText edtSugerencia = (EditText) findViewById(R.id.edtSugerencia);
        if(edtSugerencia != null){
            txtCountdown.setText(500-edtSugerencia.getText().toString().length()+"/500");
            edtSugerencia.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    txtCountdown.setText(500 - s.toString().length() + "/500");
                }
            });
        }
        Button btnEnviar = (Button) findViewById(R.id.btnEnviarSugerencia);
        if(btnEnviar != null) {
            btnEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
                    EditText edtSugerencia = (EditText) findViewById(R.id.edtSugerencia);
                    if(edtEmail != null && edtSugerencia != null) {
                        String email = edtEmail.getText().toString();
                        String sugerencia = edtSugerencia.getText().toString();
                        if(!email.isEmpty() && !sugerencia.isEmpty()) {
                            sendSugerencia(email, sugerencia);
                        } else{
                            if(email.isEmpty()){
                                edtEmail.setError("Debe ingresar un e-mail.");
                            } if(sugerencia.isEmpty()){
                                edtSugerencia.setError("Debe ingresar un mensaje.");
                            }
                        }
                    }
                }
            });
        }
    }
    private void sendSugerencia(String email, String sugerencia){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Por favor espere");
        dialog.show();

        new RequestTask(this,Util.OLD_SERVER_URL + "smartacc/addSugerencia.php", "POST", "email=" + email + "&sugerencia=" + sugerencia, new RequestTask.OnReadyCallback() {
            @Override
            public void onReady(HttpResponse response) {
                dialog.dismiss();
                if(response.code == 200){
                    new AlertDialog.Builder(SugerenciasActivity.this)
                        .setTitle("Sugerencia enviada")
                        .setMessage("Estaremos trabajando para cumplir con su pedido.")
                        .setPositiveButton("Volver a la aplicación", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SugerenciasActivity.this.startActivity(new Intent(SugerenciasActivity.this, MainActivity.class));
                            }
                        })
                        .create()
                        .show();
                } else{
                    if(response.response.equals("no internet")){
                        new AlertDialog.Builder(SugerenciasActivity.this)
                            .setTitle("Error")
                            .setMessage("Compruebe su conexión a internet.")
                            .create()
                            .show();
                    } else {
                        new AlertDialog.Builder(SugerenciasActivity.this)
                            .setTitle("Error")
                            .setMessage("Disculpe las molestias, no pudimos enviar su sugerencia. Pruebe más tarde.")
                            .create()
                            .show();
                    }
                }
            }
        }).execute();
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
