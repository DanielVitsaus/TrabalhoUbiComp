package br.com.tiradividas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.activityes.LoginActivity;
import br.com.tiradividas.util.LibraryClass;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends MainActivity {

    private static User user;
    private static final String IDUSER = "IDUSER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        user = LibraryClass.getUser();

        String id = LibraryClass.getSP(this,IDUSER);
        if (!id.isEmpty()){
            coletaDado(id);
        }
        else {
            delay();
        }

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
                    }

                }
                delay();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void delay(){

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);

                finish();
            }
        }, 1500);

    }

}