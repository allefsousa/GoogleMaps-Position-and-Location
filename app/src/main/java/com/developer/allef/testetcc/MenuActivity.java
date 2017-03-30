package com.developer.allef.testetcc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.developer.allef.testetcc.model.rua;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MenuActivity extends AppCompatActivity {

    private DatabaseReference db;
    private rua latlng;
    ArrayList<rua> lo = new ArrayList<>();

    Double latitude ;
    Double longitude;
    private  LatLng OriginBD;


    @BindView(R.id.button2) Button localidef;
    @BindView(R.id.button3) Button locaAtual;
    @BindView(R.id.button4) Button BuscaEnde;
    @BindView(R.id.button5) Button mostrarota;
    @BindView(R.id.button6) Button monitoloca;
    @BindView(R.id.button7) Button locafire;
    @BindView(R.id.button8) Button geofirebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance().getReference().child("endereco");
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        if(verificaConexao()!= false){
            locaAtual.setEnabled(false);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot dado : dataSnapshot.getChildren()){
                        latlng = dado.getValue(rua.class);
                        Toast.makeText(MenuActivity.this,"mensagem"+latlng.getLatitude(),Toast.LENGTH_LONG).show();
                        Toast.makeText(MenuActivity.this,"mensagem"+latlng.getLongitude(),Toast.LENGTH_LONG).show();
                        lo.add(latlng);


//                    if(dataSnapshot != null){
//                        for (int i = 0 ; i < lo.size(); i ++){
//                            latitude = lo.get(i).getLatitude();
//                            longitude = lo.get(i).getLongitude();
//                            Log.w("CARAIO", "Failed to read value."+ latitude + longitude );
//                            Toast.makeText(MenuActivity.this,"mensagem"+latitude,Toast.LENGTH_LONG).show();
//                            Toast.makeText(MenuActivity.this,"mensagem"+longitude,Toast.LENGTH_LONG).show();
//                            OriginBD = new LatLng(latitude,longitude);
//                        }
//                    }

                    }
                    //String longi = dataSnapshot.getValue(String.class).toString();
                    Log.d(" teste","LATITUDE" +OriginBD);
                    Log.d("Teste2","LATITUDE" +lo.toString());

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("tag", "Failed to read value.", databaseError.toException());
                }

            });
        }









        localidef.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,MainActivity.class)));

        locaAtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MenuActivity.this,LocalAtualActivity.class);
                it.putExtra("local",lo);
               startActivity(it);
            }
        });
        BuscaEnde.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,BuscaEndActivity.class)));
        monitoloca.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,MonitoraLocalGeofecinActivity.class)));
        locafire.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,LocalFireActivity.class)));
        geofirebtn.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,GeoFire.class)));
    }
    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


    }


