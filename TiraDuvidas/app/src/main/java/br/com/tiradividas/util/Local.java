package br.com.tiradividas.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.Map;

import br.com.tiradividas.Model.User;

public class Local implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String IDUSER = "IDUSER";

    private GoogleApiClient mGoogleApiClient;
    private final int MY_LOCATION_REQUEST_CODE = 1;
    private Activity activity;
    private double distancia;
    private double latitude;
    private double longetude;
    private Location location;
    private Bundle bundle;
    private Firebase firebase;
    private Map<String, Object> map;
    private LocationRequest locationRequest;
    private User user;
    private UpdateBD updateBD;


    public Local(Activity activity) {
        this.activity = activity;
        updateBD = new UpdateBD(this.activity);

        callConnection();
        map = new HashMap<>();
        firebase = LibraryClass.getFirebase();
        user = LibraryClass.getUser();
        user.setId(LibraryClass.getSP(this.activity,IDUSER));

        if (user != null){
            if(user.getId() != null){
                firebase = firebase.child("users").child(user.getId());
            }
        }
        else {
            new BuscaUser().execute();
        }

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


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            latitude = location.getLatitude();
            longetude = location.getLongitude();
            Log.i("LOG", "latitude: " + latitude);
            Log.i("LOG", "longitude: " + longetude);

            map.put("latitude", String.valueOf(location.getLatitude()));
            map.put("longetude", String.valueOf(location.getLongitude()));

            user.setLatitude(String.valueOf(location.getLatitude()));
            user.setLongetude(String.valueOf(location.getLongitude()));

            firebase.updateChildren(map);

            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
        pararConexaoComGoogleApi();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i("LOG", "latitude: " + location.getLatitude());
        Log.i("LOG", "longitude: " + location.getLongitude());

        map.put("latitude", String.valueOf(location.getLatitude()));
        map.put("longetude", String.valueOf(location.getLongitude()));

        firebase.updateChildren(map);

    }

    public void startLocationUpdates() {

        locationRequest = new LocationRequest(); //LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2500);
        //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void pararConexaoComGoogleApi() {
        //Verificando se está conectado para então cancelar a conexão!
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
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

        //this.onConnected(this.bundle);
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

    public double getLongetude() {
        return longetude;
    }

    public void setLongetude(double longetude) {
        this.longetude = longetude;
    }


    public class BuscaUser extends AsyncTask<Void,Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {
            user = updateBD.findUser(LibraryClass.getSP(activity, IDUSER));
            return null;
        }
    }
}
