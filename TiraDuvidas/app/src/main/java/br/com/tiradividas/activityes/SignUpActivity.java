package br.com.tiradividas.activityes;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;
import br.com.tiradividas.util.UpdateBD;

public class SignUpActivity extends CommonActivity {

    private static final String IDUSER = "IDUSER";
    private static final String EMAILUSER = "EMAILUSER";

    private static User user;
    private AutoCompleteTextView name;
    private AutoCompleteTextView escolaridade;
    private AutoCompleteTextView idade;
    private String matDIF;
    private String matDOM;
    private Spinner mat_dif;
    private Spinner mat_dom;
    private ProgressDialog dialog = null;
    private UpdateBD updateBD;

    private String[] matetias = new String[]{"Selecione uma Materia","Cálculo I", "Cálculo II", "Cálculo III","Física I","Física II" ,"Física III", "Algoritmo", "Química",
            "Anatomia"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateBD = new UpdateBD(this);

        mat_dif = (Spinner) findViewById(R.id.spinner_mat_dif);
        mat_dom = (Spinner) findViewById(R.id.spinner_mat_dom);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matetias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        Button cadastra = (Button) findViewById(R.id.buttom_cadas);

        if (cadastra != null) {
            cadastra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSignUpData();
                }
            });
        }

        if(mat_dom != null){
            mat_dom.setAdapter(adapter);

            mat_dom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0)
                        matDOM = matetias[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        if(mat_dif != null){
            mat_dif.setAdapter(adapter);

            mat_dif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    matDIF = matetias[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

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
        name = (AutoCompleteTextView) findViewById(R.id.edit_casda_nome);
        email = (AutoCompleteTextView) findViewById(R.id.edit_cadas_email);
        escolaridade = (AutoCompleteTextView) findViewById(R.id.edit_cadas_escolaridade);
        idade = (AutoCompleteTextView) findViewById(R.id.edit_cadas_idade);
        password = (AutoCompleteTextView) findViewById(R.id.edit_cadas_senha);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
    }

    protected void initUser(){
        user = LibraryClass.getUser();
        user.setNome( name.getText().toString() );
        user.setEmail( email.getText().toString() );
        user.setEscolaridade( escolaridade.getText().toString());
        user.setIdade( idade.getText().toString());
        user.setMateria_dificuldade(matDIF);
        user.setMateria_domoinio(matDOM);
        user.setSenha( password.getText().toString() );
        user.setLatitude("-21.7651294");
        user.setLongetude("-43.3518573");
    }

    public void sendSignUpData(  ){
        //openProgressBar();
        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        initUser();
        saveUser();

    }

    private void saveUser(){

        this.firebase.createUser(user.getEmail(),user.getSenha(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {
                        LibraryClass.saveSP(SignUpActivity.this, IDUSER , stringObjectMap.get("uid").toString());
                        LibraryClass.saveSP(SignUpActivity.this, EMAILUSER , user.getEmail());
                        user.setId( stringObjectMap.get("uid").toString() );
                        user.saveDB();
                        new SalvarUser().execute(user);
                        firebase.unauth();

                        showToast( "Conta criada com sucesso!" );
                        //closeProgressBar();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        showToast( firebaseError.getMessage() );
                        //closeProgressBar();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
        );
    }

    public void  finaliza(){
        finish();
    }

    public class SalvarUser extends AsyncTask<User,Void,Long>{

        @Override
        protected Long doInBackground(User... users) {
            return updateBD.adicionaUser(users[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            finaliza();
        }
    }
}