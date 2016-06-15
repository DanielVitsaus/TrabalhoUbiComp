package br.com.tiradividas.activityes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import br.com.tiradividas.MainActivity;
import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;

public class Localizacao extends MainActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView tvCoordinate;
    private GoogleApiClient mGoogleApiClient;
    private final int MY_LOCATION_REQUEST_CODE = 1;

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebase = LibraryClass.getFirebase();

        tvCoordinate = (TextView) findViewById(R.id.localizacao);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        callConnection();
    }

    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("LOG", "onConnected(" + bundle + ")");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(l != null){
            Log.i("LOG", "latitude: "+l.getLatitude());
            Log.i("LOG", "longitude: "+l.getLongitude());
            LatLng posicaoInicial = new LatLng(l.getLatitude(),l.getLongitude());
            LatLng posicaiFinal = new LatLng(-21.774414,-43.380779);
            double distancia = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
            tvCoordinate.setText(l.getLatitude()+" | "+l.getLongitude() + "\n"+ formatNumber(distancia));
            saveLatLon(l.getLatitude(),l.getLongitude());
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

    @Override
    protected void onStop() {
        super.onStop();
        pararConexaoComGoogleApi();
    }

    public void pararConexaoComGoogleApi() {
        //Verificando se está conectado para então cancelar a conexão!
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatNumber(double distance) {
        String unit = "m";
        if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }

    private void saveLatLon(double latitude, double longitude){

        Local local = new Local(String.valueOf(latitude),String.valueOf(latitude),LibraryClass.getSP(getBaseContext(), "TOKEN"));
        local.saveLocal();
    }

    private class Local{

        private String latitude;
        private String logetude;
        private String tokenUser;

        public Local() {
        }

        public Local(String latitude, String logetude, String tokenUser) {
            this.latitude = latitude;
            this.logetude = logetude;
            this.tokenUser = tokenUser;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLogetude() {
            return logetude;
        }

        public void setLogetude(String logetude) {
            this.logetude = logetude;
        }

        public String getTokenUser() {
            return tokenUser;
        }

        public void setTokenUser(String tokenUser) {
            this.tokenUser = tokenUser;
        }

        public void saveLocal(){
            User user = User.newUser();
            firebase = firebase.child("localizacao").child(user.getId());
            firebase.setValue(this);
            firebase.unauth();
        }
    }

}
