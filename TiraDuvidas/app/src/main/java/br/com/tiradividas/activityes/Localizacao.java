package br.com.tiradividas.activityes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import br.com.tiradividas.MainActivity;
import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.adapter.UserAdapter;
import br.com.tiradividas.adapter.UserRecyclerAdapter;
import br.com.tiradividas.adapter.UserViewHolder;
import br.com.tiradividas.chat.ChatActivity;
import br.com.tiradividas.util.CustomChildEventListener;
import br.com.tiradividas.util.CustomValueEventListener;
import br.com.tiradividas.util.LibraryClass;
import br.com.tiradividas.util.Local;

public class Localizacao extends MainActivity {

    private String nomeU = "UserChat";
    private Local local;
    private User user;
    private static List<User> users;
    private UserRecyclerAdapter adapter;
    private UserAdapter myUserAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private Firebase firebase;
    private CustomChildEventListener customChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.lista);

        users = new ArrayList<>();
        user = LibraryClass.getUser();
        local = new Local(this);

        firebase = LibraryClass.getFirebase().child("users");

        //customChildEventListener = new CustomChildEventListener();
        //firebase.addChildEventListener( customChildEventListener );

        fab = (FloatingActionButton) findViewById(R.id.fab_chat);


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


        Log.i("log", user.getId());

        firebase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("log" , "Entrou");
                for( DataSnapshot d : dataSnapshot.getChildren() ){
                    User u = d.getValue( User.class );
                    u.setId(d.getKey());
                    if (user.getId().compareTo(u.getId()) != 0) {
                        users.add(u);

                    }
                    else {
                        user = u;
                        nomeU = u.getNome();
                        Log.i("log", u.toString());
                    }
                }
                //initFirebaseAdapter();
                initUserAdapte(users);
                if (fab != null) {
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("nome", nomeU);
                            Intent intent = new Intent(Localizacao.this, ChatActivity.class);
                            intent.putExtra("nome", nomeU);
                            startActivity(intent);
                            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            //       .setAction("Action", null).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }


    private void initFirebaseAdapter(){

        Log.i("log", "ATU -> "+ user.toString());
        adapter = new UserRecyclerAdapter(User.class,R.layout.activity_localizacao,UserViewHolder.class,firebase );

        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.lista);
        if (rvUsers != null) {
            rvUsers.setHasFixedSize( true );
            rvUsers.setLayoutManager( new LinearLayoutManager(this));
            rvUsers.setAdapter(adapter);
        }
    }

    private void initUserAdapte(List<User> users){
        myUserAdapter = new UserAdapter(users, local);
        recyclerView.setLayoutManager(new LinearLayoutManager(Localizacao.this));
        recyclerView.setAdapter(myUserAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        users.clear();
        //adapter.cleanup();
        //firebase.removeEventListener( customChildEventListener );
    }


}
