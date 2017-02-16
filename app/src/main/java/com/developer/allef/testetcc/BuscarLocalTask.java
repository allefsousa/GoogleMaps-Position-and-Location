package com.developer.allef.testetcc;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

/**
 * Created by Allef on 15/02/2017.
 */

public class BuscarLocalTask  extends AsyncTaskLoader<List<Address>>{

    private Context mcontext;
    private String mLocal;
    List<Address> menderecoEncontrados;

    public BuscarLocalTask(Context context,String local) {
        super(context);
        mcontext = context;
        mLocal = local;
    }

    @Override
    protected void onStartLoading() {
        if(menderecoEncontrados == null){
            forceLoad();
        }else{
            deliverResult(menderecoEncontrados);
        }
        super.onStartLoading();
    }

    @Override
    public List<Address> loadInBackground() {
        Geocoder geocoder = new Geocoder(mcontext, Locale.getDefault());
        try{
            menderecoEncontrados = geocoder.getFromLocationName(mLocal,10);
        }catch(Exception e){
            e.printStackTrace();

        }



        return null;
    }



}
