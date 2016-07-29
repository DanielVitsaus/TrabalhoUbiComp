package br.com.tiradividas.activityes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import br.com.tiradividas.MainActivity;
import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;

public class Perfil extends MainActivity {
    Firebase firebase = LibraryClass.getFirebase();
    private static User user = LibraryClass.getUser();
    private ProgressDialog dialog = null;
    private AutoCompleteTextView name;
    private AutoCompleteTextView email;
    private AutoCompleteTextView escolaridade;
    private AutoCompleteTextView idade;
    private AutoCompleteTextView password;
    private String matDIF;
    private String matDOM;
    private Spinner mat_dif;
    private Spinner mat_dom;
    private String[] materias = new String[]{"Selecione uma Materia","Cálculo I", "Cálculo II", "Cálculo III","Física I","Física II" ,"Física III", "Algoritmo", "Química",
            "Anatomia"};

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
        name = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        name.setText(user.getNome());

        email = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        email.setText(user.getEmail());

        escolaridade = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
        escolaridade.setText(user.getEscolaridade());

        idade = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView4);
        idade.setText(user.getIdade());

        password = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView5);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, materias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        mat_dif = (Spinner) findViewById(R.id.spinner);
        mat_dom = (Spinner) findViewById(R.id.spinner2);

        mat_dif.setAdapter(adapter);
        mat_dif.setSelection(adapter.getPosition(user.getMateria_dificuldade()));
        mat_dif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                matDIF = materias[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mat_dom.setAdapter(adapter);

        mat_dom.setSelection(adapter.getPosition(user.getMateria_domoinio()));
        mat_dom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    matDOM = materias[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button cadastra = (Button) findViewById(R.id.button);

        if (cadastra != null) {
            cadastra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendPerfilData();
                }
            });
        }
    }

    public void sendPerfilData(  ){
        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Salvando...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        this.alterUser();
    }

    protected void alterUser(){
        user.setNome( name.getText().toString() );
        user.setEmail( email.getText().toString() );
        user.setEscolaridade( escolaridade.getText().toString());
        user.setIdade( idade.getText().toString());
        user.setMateria_dificuldade(matDIF);
        user.setMateria_domoinio(matDOM);
        if(password.getText().length()>0) {
            user.setSenha(password.getText().toString());
        }
        user.saveDB();
        dialog.hide();

    }

    private void saveUser(){

        this.firebase.createUser(user.getEmail(),user.getSenha(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {
                        user.setId( stringObjectMap.get("uid").toString() );
                        user.saveDB();
                        firebase.unauth();

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        finish();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
        );
    }

}