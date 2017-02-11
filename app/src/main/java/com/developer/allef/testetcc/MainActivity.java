package com.developer.allef.testetcc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author Allef
 * 
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng mOrigen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * Chamada de metodo para inicializar mapa
         */
        inicializaMapa();

        //region Thiengo Script MapFragment
        /**
         * Inicializando mapa via scrip Thiengo
         */
        /*

        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.zOrderOnTop(true);

        SupportMapFragment fragment = SupportMapFragment.newInstance(mapOptions);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.llmap,fragment);
        ft.commit();
    */
        //endregion
    }

    /**
     * Metodo criando para inicializar o Maps
     */
    private void inicializaMapa() {
        if(map == null){
            SupportMapFragment fragmentMap = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            fragmentMap.getMapAsync(this);
        }




    }

    /**
     * Metodo para personalização do Maps
     * type , local , anim
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // tratando a imagem a ser usada como marcador
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);



        // recuperando objeto GoogleMap
        map = googleMap;
        // desativando recurso para abrir o aplicativo do google maps
        map.getUiSettings().setMapToolbarEnabled(false);
        //habilitando zoom e desabilitando gestos no mapa
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(false);

        // latitude e longitude
        mOrigen = new LatLng(-23.561706,-46.655981);
        // stylo do mapa
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // animação da camera com latitude e proximidade da camera que vai de 2 a 21
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigen,17.0f));
        // adicionando marcador no Maps
        map.addMarker(new MarkerOptions()
        .position(mOrigen)
        .icon(icon)
        .title("Av Paulista ")
        .snippet("Sampa"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mOrigen)
                .zoom(17)
                .bearing(90)
                .tilt(45)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}
