package com.developer.allef.testetcc;

import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuscaEndActivity extends AppCompatActivity {
    @BindView(R.id.textInputLayout)TextInputLayout LabelErro;
    @BindView(R.id.txtendereco)EditText txtendereco;
    @BindView(R.id.buscar) Button btnbuscar;
    @BindView(R.id.latitude)TextView latitude;
    @BindView(R.id.longitude)TextView longitude;
    Geocoder gc = new Geocoder(this,new Locale("pt","BR"));
    List<Address> list;
    Address ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_end);
        ButterKnife.bind(this);

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    list = gc.getFromLocationName(txtendereco.getText().toString(),10);
                   latitude.setText(list.toString()
                   );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });





    }
}
