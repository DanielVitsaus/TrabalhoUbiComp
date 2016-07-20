package br.com.tiradividas.activityes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import java.util.ArrayList;
import java.util.List;

import br.com.tiradividas.MainActivity;
import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.adapter.UserAdapter;
import br.com.tiradividas.util.LibraryClass;
import br.com.tiradividas.util.Local;

public class Localizacao extends MainActivity {

    private Local local;
    private static User user;
    private static List<User> users;
    //private UserRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressDialog dialog = null;
    private TextView nomeuser;

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tira Duvidas");
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.lista);
        user = LibraryClass.getUser();

        users = new ArrayList<>();

        firebase = LibraryClass.getFirebase().child("users");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            View view = navigationView.getHeaderView(0);
            nomeuser = (TextView)view.findViewById(R.id.nomeuser);
            nomeuser.setText(user.getNome());
            ((TextView)view.findViewById(R.id.emailuser)).setText(user.getEmail());
            navigationView.setNavigationItemSelectedListener(this);
        }

        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

    }

    private void initUserAdapte(List<User> users) {
        Log.i("log", "User novo -> "+user.toString());
        UserAdapter myUserAdapter = new UserAdapter(this, user, users, local);
        recyclerView.setLayoutManager(new LinearLayoutManager(Localizacao.this));
        recyclerView.setAdapter(myUserAdapter);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @Override
    protected void onStart() {
        local = new Local(this);
        super.onStart();

        firebase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();

                Log.i("log" , "Entrou");
                for( DataSnapshot d : dataSnapshot.getChildren() ){
                    User u = d.getValue( User.class );
                    u.setId(d.getKey());
                    local.calculaDistancia(
                            Double.valueOf(user.getLatitude()),Double.valueOf(user.getLongetude()),
                            Double.valueOf(u.getLatitude()),Double.valueOf(u.getLongetude()));

                    if (user.getId().compareTo(u.getId()) != 0 && local.getDistancia() < 100) {
                        users.add(u);
                    }
                    if (user.getId().compareTo(u.getId()) == 0){
                        nomeuser.setText(u.getNome());
                        user = u;
                    }
                }
                initUserAdapte(users);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        local.pararConexaoComGoogleApi();
        super.onPause();
    }

    @Override
    protected void onStop() {
        users.clear();
        local.pararConexaoComGoogleApi();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        users.clear();
        local.pararConexaoComGoogleApi();
        super.onDestroy();
    }


}
