package br.com.tiradividas.activityes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;

public class SemInternet extends AppCompatActivity {

    private static User user;
    private static final String IDUSER = "IDUSER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sem_internet);

        user = LibraryClass.getUser();

        String id = LibraryClass.getSP(this,IDUSER);

        new TestaNet().execute(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void coletaDado(final String id){

        Firebase firebase = LibraryClass.getFirebase();

        firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for( DataSnapshot d : dataSnapshot.getChildren() ) {
                    User u =  d.getValue(User.class);

                    if (id.compareTo(d.getKey()) == 0){
                        user.setNome(u.getNome());
                        user.setEmail(u.getEmail());
                        user.setEscolaridade(u.getEscolaridade());
                        user.setIdade(u.getIdade());
                        user.setMateria_dificuldade(u.getMateria_dificuldade());
                        user.setMateria_domoinio(u.getMateria_domoinio());
                        user.setLatitude(u.getLatitude());
                        user.setLongetude(u.getLongetude());
                        user.setLista_chat(u.getLista_chat());
                        user.setId(d.getKey());
                        break;
                    }

                }
                Intent i = new Intent(SemInternet.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private class TestaNet extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... paranet) {
            while (true) {
                if (isConnectingToInternet()) {
                    coletaDado(paranet[0]);
                    break;
                }
            }
            return null;
        }

    }

}
