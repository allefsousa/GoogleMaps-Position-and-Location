package com.developer.allef.testetcc;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.developer.allef.testetcc.model.rua;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private  LatLng OriginBD;
    private DatabaseReference db;
    private rua latlng;
    ArrayList<rua> lo = new ArrayList<>();
    Double latitude ;
    Double longitude;
    private rua lovaga;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_atual);
        db = FirebaseDatabase.getInstance().getReference().child("endereco");
        lovaga = new rua();


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot dado : dataSnapshot.getChildren()){
                    latlng = dado.getValue(rua.class);
                    Toast.makeText(LocalAtualActivity.this,"mensagem"+latlng.getLatitude(),Toast.LENGTH_LONG).show();
                    Toast.makeText(LocalAtualActivity.this,"mensagem"+latlng.getLongitude(),Toast.LENGTH_LONG).show();
                    lo.add(latlng);
                    if(dataSnapshot != null){
                        for (int i = 0 ; i < lo.size(); i ++){
                            latitude = lo.get(i).getLatitude();
                            longitude = lo.get(i).getLongitude();
                            Log.w("CARAIO", "Failed to read value."+ latitude + longitude );
                            Toast.makeText(LocalAtualActivity.this,"mensagem"+latitude,Toast.LENGTH_LONG).show();
                            Toast.makeText(LocalAtualActivity.this,"mensagem"+longitude,Toast.LENGTH_LONG).show();
                            OriginBD = new LatLng(latitude,longitude);
                        }
                    }

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




//        OriginBD = new LatLng(-23.561706,-46.655981);


        mhandler = new Handler();
        mdeveexibirdialog = savedInstanceState == null;
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        InicializaMapa();



        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        verificarStatusGPS();


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
        }else if(requestCode == Request_chekcar_gps){
            if(resultCode == RESULT_OK){
                mtentativas = 0;
                mhandler.removeCallbacksAndMessages(null);
                obterUltimaLocalizacao();
            }else{
                Toast.makeText(this,R.string.erro_gps,Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        lo = (ArrayList<rua>) intent.getSerializableExtra("local");
        Log.d("ALLEFSOUSA","Location :" + lo.get(0).getLatitude()+" "+ lo.get(0).getLongitude());



    }

    @Override
    protected void onStop() {
        if (mgoogleApiClient != null && mgoogleApiClient.isConnected()) {
            mgoogleApiClient.disconnect();
        }
        mhandler.removeCallbacksAndMessages(null);

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
            mtentativas = 0;
            mOrigen = new LatLng(location.getLatitude(),location.getLongitude());
            atualizarMapa();
        }else if(mtentativas < 10){
            mtentativas++;
            mhandler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    obterUltimaLocalizacao();
                }
            },2000);
        }

    }

    private void atualizarMapa() {


        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigen,17.0f));
        mgoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mgoogleMap.clear();

        mgoogleMap.addMarker(new MarkerOptions()
            .position(mOrigen)
            .title("Local Atual"));
        Log.d("mOrigem","mOrigem" +mOrigen);

        mgoogleMap.addMarker(new MarkerOptions()
                .position(OriginBD)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.preferencial))
                .title("Vaga Prioritaria"));


    }
    private LatLng tarzerdados(){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dado : dataSnapshot.getChildren()){
                    latlng = dado.getValue(rua.class);
                    lo.add(latlng);
                    OriginBD = new LatLng(latlng.getLatitude(),latlng.getLongitude());
                }
                //String longi = dataSnapshot.getValue(String.class).toString();
                Log.d("ALLEF","LATITUDE" +OriginBD);
                Log.d("ALLEF","LATITUDE" +lo.toString());




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("tag", "Failed to read value.", databaseError.toException());

            }
        });
        return OriginBD;

    }
    private void adicionarPoly(GoogleMap map,LatLng latLng, LatLng latLng2){

        // desenhar a linha entre os dois Pontos
        PolylineOptions line = new PolylineOptions();
        line.add(new LatLng(latLng.latitude,latLng.longitude));
        line.add(new LatLng(latLng2.latitude,latLng2.longitude));
        line.color(Color.RED);
        Polyline polyline = mgoogleMap.addPolyline(line);
        polyline.setGeodesic(true);

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
        mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mgoogleMap.getUiSettings().setMapToolbarEnabled(true);

    }

    private void verificarStatusGPS(){
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder locationSettingsRequest = new LocationSettingsRequest.Builder();
        locationSettingsRequest.setAlwaysShow(true);
        locationSettingsRequest.addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mgoogleApiClient,locationSettingsRequest.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch(status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                      //  obterUltimaLocalizacao();
                    break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if(mdeveexibirdialog){
                            try{
                                status.startResolutionForResult(LocalAtualActivity.this,Request_chekcar_gps);
                                mdeveexibirdialog = false;
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


        });
    }



}
