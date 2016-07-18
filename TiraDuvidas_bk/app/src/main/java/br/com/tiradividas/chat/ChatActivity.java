package br.com.tiradividas.chat;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;

import br.com.tiradividas.Model.Chat;
import br.com.tiradividas.R;
import br.com.tiradividas.activityes.Localizacao;
import br.com.tiradividas.util.LibraryClass;


public class ChatActivity extends AppCompatActivity {

    private String mUsername;
    private String idChat;
    private String idamigo;
    private String iduser;
    private Firebase mFirebaseRef;
    private Firebase firebase;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;
    private Bundle dados;
    private String mId;

    private static final String KEY_CHAT = "ChatPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acticity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        dados = intent.getExtras();

        mId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Make sure we have a mUsername
        setupUsername();

        //setTitle("Chatting as " + mUsername);
        setTitle("CHAT");
        if (dados.getString("iduser") != null) {
            //iduser = dados.getString("iduser").subSequence(0, 10).toString();
            iduser = dados.getString("iduser");
        }

        if (dados.getString("idchat") != null) {
            //idamigo = dados.getString("idamigo").subSequence(0,10).toString();
            idChat = dados.getString("idchat");
        }

        //idChat = idamigo;

        mFirebaseRef = LibraryClass.getFirebase_chat().child("chat").child(idChat);

        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = (ListView) findViewById(R.id.list);//getListView();
        // Tell our list adapter that we only want 50 messages at a time
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limitToLast(5000), this, R.layout.chat_message, mUsername);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(ChatActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void setupUsername() {
        //LibraryClass.saveSP(this, KEY_CHAT, mUsername);
        //SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = LibraryClass.getSP(this,KEY_CHAT);
        String[] part = null;
        if (mUsername.isEmpty()) {
            Random r = new Random();
            // Assign a random user name if we don't have one saved.
            part = dados.getString("nome").split(" ");
            mUsername = part[0] +"_"+ r.nextInt(100000);
            LibraryClass.saveSP(this, KEY_CHAT, mUsername);
        }
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername, mId);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Localizacao.class);
        startActivity(intent);
        finish();
    }
}
