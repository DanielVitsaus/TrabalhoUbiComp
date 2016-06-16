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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import br.com.tiradividas.adapter.UserRecyclerAdapter;
import br.com.tiradividas.adapter.UserViewHolder;
import br.com.tiradividas.util.CustomChildEventListener;
import br.com.tiradividas.util.CustomValueEventListener;
import br.com.tiradividas.util.LibraryClass;
import br.com.tiradividas.util.Local;

public class Localizacao extends MainActivity {

    private Local local;
    private UserRecyclerAdapter adapter;

    private Firebase firebase;
    private CustomChildEventListener customChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        local = new Local(this);

        firebase = LibraryClass.getFirebase().child("users");

        customChildEventListener = new CustomChildEventListener();
        firebase.addChildEventListener( customChildEventListener );


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

        init();
    }


    private void init(){
        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.lista);
        rvUsers.setHasFixedSize( true );
        rvUsers.setLayoutManager( new LinearLayoutManager(this));

        adapter = new UserRecyclerAdapter(
                User.class,
                R.layout.activity_localizacao,
                UserViewHolder.class,
                firebase );

        rvUsers.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
        firebase.removeEventListener( customChildEventListener );
    }


}
