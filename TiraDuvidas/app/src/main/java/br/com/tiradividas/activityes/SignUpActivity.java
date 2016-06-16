package br.com.tiradividas.activityes;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;

public class SignUpActivity extends CommonActivity {

    private User user;
    private EditText name;
    private EditText escolaridade;
    private EditText idade;
    private EditText mat_dif;
    private EditText mat_dom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button cadastra = (Button) findViewById(R.id.buttom_cadas);

        if (cadastra != null) {
            cadastra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSignUpData();
                }
            });
        }

        this.firebase = LibraryClass.getFirebase();
        initViews();
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
        name = (EditText) findViewById(R.id.edit_casda_nome);
        email = (EditText) findViewById(R.id.edit_cadas_email);
        escolaridade = (EditText) findViewById(R.id.edit_cadas_escolaridade);
        idade = (EditText) findViewById(R.id.edit_cadas_idade);
        mat_dif = (EditText) findViewById(R.id.edit_cadas_mat_dif);
        mat_dom = (EditText) findViewById(R.id.edit_cadas_mat_dom);
        password = (EditText) findViewById(R.id.edit_cadas_senha);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
    }

    protected void initUser(){
        user = LibraryClass.getUser();
        user.setNome( name.getText().toString() );
        user.setEmail( email.getText().toString() );
        user.setEscolaridade( escolaridade.getText().toString());
        user.setIdade( idade.getText().toString());
        user.setMateria_dificuldade(mat_dif.getText().toString());
        user.setMateria_domoinio(mat_dom.getText().toString());
        user.setSenha( password.getText().toString() );
    }

    public void sendSignUpData(  ){
        openProgressBar();
        initUser();
        saveUser();
    }

    private void saveUser(){

        this.firebase.createUser(user.getEmail(),user.getSenha(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {
                        user.setId( stringObjectMap.get("uid").toString() );
                        user.saveDB();
                        firebase.unauth();

                        showToast( "Conta criada com sucesso!" );
                        closeProgressBar();
                        finish();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        showSnackbar( firebaseError.getMessage() );
                        closeProgressBar();
                    }
                }
        );
    }

}