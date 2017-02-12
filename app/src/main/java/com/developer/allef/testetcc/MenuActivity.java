package com.developer.allef.testetcc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {
    private Button b1,b2,b3,b4,b5,b6;


    @BindView(R.id.button2) Button localidef;
    @BindView(R.id.button3) Button locaAtual;
    @BindView(R.id.button4) Button BuscaEnde;
    @BindView(R.id.button5) Button mostrarota;
    @BindView(R.id.button6) Button monitoloca;
    @BindView(R.id.button7) Button locafire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        localidef.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,MainActivity.class)));
        locaAtual.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,LocalAtualActivity.class)));
        BuscaEnde.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,BuscaEndActivity.class)));
        mostrarota.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,RotaActivity.class)));
        monitoloca.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,MonitoraLocalGeofecinActivity.class)));
        locafire.setOnClickListener(view -> startActivity(new Intent(MenuActivity.this,LocalFireActivity.class)));







    }
}
