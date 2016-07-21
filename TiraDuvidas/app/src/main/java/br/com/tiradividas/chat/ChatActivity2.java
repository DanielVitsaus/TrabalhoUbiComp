package br.com.tiradividas.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.collection.LLRBNode;

import java.util.ArrayList;
import java.util.List;

import br.com.tiradividas.Model.Chat;
import br.com.tiradividas.R;
import br.com.tiradividas.adapter.ChatAdapter;
import br.com.tiradividas.util.FirebaseInstanceIDService;
import br.com.tiradividas.util.LibraryClass;

public class ChatActivity2 extends AppCompatActivity {

    private static final String TAG = ChatActivity2.class.getName();
    private static final String TOKEN_NOTFI = "TOKEN_APP";

    private EditText metText;
    private ImageButton mbtSent;
    private Firebase mFirebaseRef;

    private List<Chat> mChats;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private String mId;
    private Bundle dados;
    private String idChat;
    private String nomeuser;
    private String nomeamigo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        Intent intent = getIntent();
        dados = intent.getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (dados.getString("nome") != null) {
            nomeuser = dados.getString("nome");
        }

        if (dados.getString("nomeamigo") != null){
            nomeamigo = dados.getString("nomeamigo");
            Log.i("log", nomeamigo);
            toolbar.setTitle(dados.getString("nomeamigo"));
            toolbar.setTitleTextColor(Color.WHITE);
        }

        if (dados.getString("idchat") != null) {
            idChat = dados.getString("idchat");
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        metText = (EditText) findViewById(R.id.etText);
        mbtSent = (ImageButton) findViewById(R.id.btSent);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvChat);
        mChats = new ArrayList<>();

        mId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));
        mAdapter = new ChatAdapter(mChats, mId);
        mRecyclerView.setAdapter(mAdapter);


        /**
         * Firebase - Inicialize
         */
        mFirebaseRef = LibraryClass.getFirebase_chat().child("chat").child(idChat);


        mbtSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = metText.getText().toString();

                if (!message.isEmpty()) {
                    /**
                     * Firebase - Send message
                     */
                    mFirebaseRef.push().setValue(new Chat(message, nomeuser,mId));
                    Log.i("RES", "ENVIADO");
                    FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();

                    firebaseInstanceIDService.enviaInfo("add",LibraryClass.getSP(ChatActivity2.this, TOKEN_NOTFI),
                            idChat, LibraryClass.getUser().getId(), "Uma nova mensagem te espera.");

                    firebaseInstanceIDService.enviaInfo("send",LibraryClass.getSP(ChatActivity2.this, TOKEN_NOTFI),
                            idChat, LibraryClass.getUser().getId(), "Seu amigo precisa de você.!"+nomeuser);
                    //envia msg para o serdidor dizendo que mando um nova mensagem
                }

                metText.setText("");
            }
        });


        /**
         * Firebase - Receives message
         */

    }

    @Override
    protected void onStart() {

        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try{
                        Chat model = dataSnapshot.getValue(Chat.class);

                        mChats.add(model);
                        mRecyclerView.scrollToPosition(mChats.size() - 1);
                        mAdapter.notifyItemInserted(mChats.size() - 1);

                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        mFirebaseRef.unauth();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mFirebaseRef.unauth();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.it_anexo) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}