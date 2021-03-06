package br.com.tiradividas.activityes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.FirebaseInstanceIDService;
import br.com.tiradividas.util.LibraryClass;
import br.com.tiradividas.util.Local;

public class LoginActivity extends CommonActivity  {

    private static final String IDUSER = "IDUSER";
    private static final String NOME = "NOME";

    private static User user;
    private AutoCompleteTextView email;
    private EditText senha;
    private Local local = null;
    private ProgressDialog dialog = null;
    private boolean dia = false;

    private FirebaseInstanceIDService firebaseInstanceIDService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseInstanceIDService = new FirebaseInstanceIDService();
        firebaseInstanceIDService.setContext(this.getApplicationContext());
        user = LibraryClass.getUser();
        user.setLatitude("-21.7651294");
        user.setLongetude("-43.3518573");

        firebase = LibraryClass.getFirebase();

        Button login = (Button) findViewById(R.id.email_sign_in_button);
        if (login != null) {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dia = true;
                    sendLoginData();
                }
            });
        }

        initViews();
        verifyUserLogged();


    }

   @Override
    protected void onStart() {
       super.onStart();
       firebaseInstanceIDService.onTokenRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(local != null) {
            local.pararConexaoComGoogleApi();
        }
        super.onDestroy();
    }

    protected void initViews(){
        email = (AutoCompleteTextView) findViewById(R.id.email);
        senha = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
    }

    protected void initUser(){
        user.setId(LibraryClass.getSP(LoginActivity.this, IDUSER));
        user.setEmail( email.getText().toString() );
        user.setSenha( senha.getText().toString() );
    }

    public void callSignUp(View view){
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity(intent);
    }


    public void sendLoginData(){
        //openProgressBar();
        if(dia) {
            this.dialog = new ProgressDialog(this);
            dialog.setMessage("Carregando...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            dia = false;
        }
        initUser();
        verifyLogin();
    }


    private void callMainActivity(){
        //if (user.getLatitude().length() == 0 && user.getLongetude().length() == 0){
        local = new Local(this);
        //}
        Intent intent = new Intent( this, Localizacao.class );
        startActivity(intent);
        finish();
    }

    private void verifyUserLogged(){

        if( firebase.getAuth() != null ){

            LibraryClass.saveSP(LoginActivity.this, IDUSER , firebase.getAuth().getUid());
            user.setId(firebase.getAuth().getUid());
            //initUser();
            callMainActivity();
        }
        else{
            //initUser();
            if( !user.getTokenSP(this).isEmpty() ){
                firebase.authWithCustomToken(
                        user.getTokenSP(this),
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                user.saveTokenSP( LoginActivity.this, authData.getToken() );
                                LibraryClass.saveSP(LoginActivity.this, IDUSER , authData.getUid());
                                user.setId(authData.getUid());
                                //initUser();
                                callMainActivity();
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) { }
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
                        LibraryClass.saveSP(LoginActivity.this, IDUSER , authData.getUid());
                        user.setId(authData.getUid());
                        Local local = new Local(LoginActivity.this);
                        Map<String, Object> nome  = authData.getProviderData();
                        Log.i("log", nome.toString());
                        //initUser();
                        //closeProgressBar();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            //local.pararConexaoComGoogleApi();
                        }
                        callMainActivity();

                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        showSnackbar( firebaseError.getMessage() );
                        //closeProgressBar();
                        //dialog.setMessage(firebaseError.getMessage());
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
        );
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}