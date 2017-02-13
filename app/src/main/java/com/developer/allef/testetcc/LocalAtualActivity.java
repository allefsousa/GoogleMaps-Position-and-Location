package com.developer.allef.testetcc;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.app.Activity.RESULT_OK;

public class LocalAtualActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    //region variaveis
    private static final int REQUEST_ERROR_PLAY_SERVICES = 1;
    private GoogleMap mgoogleMap;
    private GoogleApiClient mgoogleApiClient;
    private static final int Request_chekcar_gps = 2;
    private static final String Extra_Dialog = "dialog";
    private Handler mhandler;
    private boolean mdeveexibirdialog;
    private int mtentativas;
    private LatLng mOrigen;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_atual);
        mhandler = new Handler();
        mdeveexibirdialog = savedInstanceState == null;
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        InicializaMapa();



        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

       // verificarStatusGPS();


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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(Extra_Dialog,mdeveexibirdialog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mdeveexibirdialog = savedInstanceState.getBoolean(Extra_Dialog,true);
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
        verificarStatusGPS();
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
        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigen,17.0f));
        mgoogleMap.clear();

        mgoogleMap.addMarker(new MarkerOptions()
            .position(mOrigen)
            .title("Local Atual"));

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
        final String TAG = "DIALOG_ERROR_PLAY_SERVICES";
        if(getSupportFragmentManager().findFragmentByTag(TAG) == null){

            DialogFragment errorDialogFragment = new DialogFragment(){
                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return GooglePlayServicesUtil.getErrorDialog(errorCode,getActivity(),REQUEST_ERROR_PLAY_SERVICES);
                }
            };
            errorDialogFragment.show(fragmentActivity.getSupportFragmentManager(),TAG);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // inicializando o mapa para modificações
        mgoogleMap = googleMap;
        mgoogleMap.getUiSettings().setMapToolbarEnabled(true);

    }

    private void verificarStatusGPS(){
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder locationSettingsRequest = new LocationSettingsRequest.Builder();
        locationSettingsRequest.setAlwaysShow(true);
        locationSettingsRequest.addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mgoogleApiClient,locationSettingsRequest.build());

        result.setResultCallback(new ResultCallbacks<LocationSettingsResult>() {
            @Override
            public void onSuccess(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch(status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        obterUltimaLocalizacao();
                    break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if(mdeveexibirdialog){
                            try{
                                status.startResolutionForResult(LocalAtualActivity.this,Request_chekcar_gps);
                            }catch(IntentSender .SendIntentException e){
                                e.printStackTrace();

                            }
                        }
                    break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.wtf("NGVL","iSSO NAO DEVERIA ACONTECER");
                        break;

                }
            }

            @Override
            public void onFailure(@NonNull Status status) {

            }

        });
    }



}
