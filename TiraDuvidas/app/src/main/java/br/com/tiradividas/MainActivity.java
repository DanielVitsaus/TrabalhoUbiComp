package br.com.tiradividas;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

import br.com.tiradividas.activityes.Agenda;
import br.com.tiradividas.activityes.Forum;
import br.com.tiradividas.activityes.Localizacao;
import br.com.tiradividas.activityes.LoginActivity;
import br.com.tiradividas.activityes.MinhasDuvidas;
import br.com.tiradividas.activityes.Perfil;
import br.com.tiradividas.util.LibraryClass;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TOKEN = "TOKEN";

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            finish();
            Intent intent  = new Intent(this.getApplication(), Perfil.class);
            startActivity(intent);

        } else if (id == R.id.nav_camera) {
            finish();
            Intent intent  = new Intent(this.getApplication(), Forum.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            finish();
            Intent intent  = new Intent(this.getApplication(), MinhasDuvidas.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            finish();
            Intent intent  = new Intent(this.getApplication(), Localizacao.class);
            startActivity(intent);
        }else if (id == R.id.nav_manage) {
            finish();
            Intent intent  = new Intent(this.getApplication(), Agenda.class);
            startActivity(intent);
        }else if (id == R.id.loguot) {
            Firebase firebase = LibraryClass.getFirebase();
            firebase.unauth();
            LibraryClass.removeSP(this, TOKEN);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
