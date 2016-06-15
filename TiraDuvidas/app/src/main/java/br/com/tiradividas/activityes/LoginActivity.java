package br.com.tiradividas.activityes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;

public class LoginActivity extends CommonActivity {

    private User user;
    private AutoCompleteTextView email;
    private EditText senha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.email_sign_in_button);
        if (login != null) {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendLoginData();
                }
            });
        }

        firebase = LibraryClass.getFirebase();
        initViews();
        verifyUserLogged();
    }

   @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void initViews(){
        email = (AutoCompleteTextView) findViewById(R.id.email);
        senha = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
    }

    protected void initUser(){
        user = User.newUser();
        user.setEmail( email.getText().toString() );
        user.setSenha( senha.getText().toString() );
    }

    public void callSignUp(View view){
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity(intent);
    }


    public void sendLoginData(){
        openProgressBar();
        initUser();
        verifyLogin();
    }


    private void callMainActivity(){
        Intent intent = new Intent( this, Forum.class );
        startActivity(intent);
        finish();
    }

    private void verifyUserLogged(){
        if( firebase.getAuth() != null ){
            callMainActivity();
        }
        else{
            initUser();

            if( !user.getTokenSP(this).isEmpty() ){
                firebase.authWithPassword(
                        "password",
                        user.getTokenSP(this),
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                user.saveTokenSP( LoginActivity.this, authData.getToken() );
                                callMainActivity();
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {}
                        }
                );
            }
        }
    }

    private void verifyLogin(){
        firebase.authWithPassword(
                user.getEmail(),
                user.getSenha(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        user.saveTokenSP( LoginActivity.this, authData.getToken() );
                        user.setId(authData.getUid());
                        showToast(authData.getUid());
                        closeProgressBar();
                        callMainActivity();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        showSnackbar( firebaseError.getMessage() );
                        closeProgressBar();
                    }
                }
        );
    }

}