package br.com.tiradividas.activityes;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.tiradividas.MainActivity;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;

public class Perfil extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            ((TextView)view.findViewById(R.id.nomeuser)).setText(LibraryClass.getUser().getNome());
            ((TextView)view.findViewById(R.id.emailuser)).setText(LibraryClass.getUser().getEmail());
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

}
