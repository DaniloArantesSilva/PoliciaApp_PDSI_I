package com.example.danilo.policiaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class ActivityAbreOcorrencia extends AppCompatActivity {

    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abreocorrencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAbreOcorrencia);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

                if(!service.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    AlertNoGps();
                }

                if(service.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    finish();
                }

            }
        });

        //Adiciona o botão "up navigation" (voltar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            abandona();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abandona() {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Atenção");
        //define a mensagem
        builder.setMessage("Realmente deseja abortar a abertura da Ocorrência ?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.cancel();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
        alerta.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        alerta.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }

    private void AlertNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Para continuar, permita que o dispositivo ative a localização")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id){
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id){
                        dialog.cancel();
                    }
                });
        alerta = builder.create();
        alerta.show();
    }


}
