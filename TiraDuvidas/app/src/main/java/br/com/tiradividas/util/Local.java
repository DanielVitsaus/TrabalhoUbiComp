package br.com.tiradividas.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

/**
 * Created by danielcandido on 16/06/16.
 */
public class Local implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private final int MY_LOCATION_REQUEST_CODE = 1;
    private Activity activity;
    private double distancia;
    private double latitude;
    private double logetude;
    private Location location;
    private Bundle bundle;


    public Local(Activity activity) {
        this.activity = activity;
        callConnection();
    }

    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        this.bundle = bundle;
        Log.i("LOG", "onConnected(" + bundle + ")");


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location != null){
            latitude = location.getLatitude();
            logetude = location.getLongitude();
            Log.i("LOG", "latitude: "+latitude);
            Log.i("LOG", "longitude: "+logetude);
            //LatLng posicaoInicial = new LatLng(latitude,logetude);
            //LatLng posicaiFinal = new LatLng(-21.774414,-43.380779);
            //distancia = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed("+connectionResult+")");
        pararConexaoComGoogleApi();
    }

    public void pararConexaoComGoogleApi() {
        //Verificando se está conectado para então cancelar a conexão!
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressLint("DefaultLocale")
    public String formatNumber(double distance) {
        String unit = "m";
        if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%3.2f%s", distance, unit);
    }

    public String calculaDistancia(double lat_this, double log_this, double lat_final, double log_final){

        LatLng posicaoInicial = new LatLng(lat_this,log_this);
        LatLng posicaiFinal = new LatLng(lat_final,log_final);
        distancia = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);

        return formatNumber(distancia);
    }

    public Location getLocation() {
        this.onConnected(this.bundle);
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLogetude() {
        return logetude;
    }

    public void setLogetude(double logetude) {
        this.logetude = logetude;
    }
}
