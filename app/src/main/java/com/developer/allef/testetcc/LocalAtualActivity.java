package com.developer.allef.testetcc;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class LocalAtualActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private static final int REQUEST_ERROR_PLAY_SERVICES = 1;
    private GoogleMap mgoogleMap;
    private GoogleApiClient mgoogleApiClient;
    private LatLng mOrigen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_atual);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        InicializaMapa();

        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();


    }

    @Override
    protected void onStart() {

        super.onStart();
        mgoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ERROR_PLAY_SERVICES && resultCode == RESULT_OK) {
            mgoogleApiClient.connect();
        }


    }

    @Override
    protected void onStop() {
        if (mgoogleApiClient != null && mgoogleApiClient.isConnected()) {
            mgoogleApiClient.disconnect();
        }

        super.onStop();
    }


    private void InicializaMapa() {

        if (mgoogleMap == null) {
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
            fragment.getMapAsync(this);
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        obterUltimaLocalizacao();
    }

    private void obterUltimaLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if(location != null){
            mOrigen = new LatLng(location.getLatitude(),location.getLongitude());
            atualizarMapa();
        }

    }

    private void atualizarMapa() {



    }

    @Override
    public void onConnectionSuspended(int i) {
        mgoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()){

            try{
                connectionResult.startResolutionForResult(this,REQUEST_ERROR_PLAY_SERVICES);

            }catch(IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }else{
            exibirMensagemDeErro(this,connectionResult.getErrorCode());
        }
    }

    private void exibirMensagemDeErro(FragmentActivity fragmentActivity, int errorCode) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // inicializando o mapa para modificações
        mgoogleMap = googleMap;
        mgoogleMap.getUiSettings().setMapToolbarEnabled(true);





    }
}
