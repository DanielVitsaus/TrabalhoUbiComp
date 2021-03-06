package br.com.tiradividas.chat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
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
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int SELECT_PICTURE = 2;
    private static final int SELECT_DOCUMENT = 3;

    private String selectedImagePath;
    private String selectedDocumentPath;

    private EditText metText;
    private ImageButton mbtSent;
    private Firebase mFirebaseRef;
    private StorageReference storageRef;

    private List<Chat> mChats;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private String mId;
    private Bundle dados;
    private String idChat;
    private String nomeuser;
    private String nomeamigo;
    private String uri;
    private File imageFile;
    private String tokenAPP;


    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        tokenAPP = LibraryClass.getSP(ChatActivity2.this,TOKEN_NOTFI);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_teste);

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

        mFirebaseRef = LibraryClass.getFirebase_chat().child("chat").child(idChat);

        mAdapter = new ChatAdapter(mChats, mId, mFirebaseRef);
        mRecyclerView.setAdapter(mAdapter);

        FirebaseStorage storage = LibraryClass.getStorage();
        storageRef = storage.getReferenceFromUrl("gs://chatduvidas.appspot.com");



        mbtSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = metText.getText().toString();


                if (!message.isEmpty()) {
                    //preencher = true;
                    mFirebaseRef.push().setValue(new Chat(message, nomeuser,mId, "0"));

                    FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();

                    firebaseInstanceIDService.enviaInfo("add",tokenAPP,
                            idChat, LibraryClass.getUser().getId(), "Uma nova mensagem te espera.");

                    firebaseInstanceIDService.enviaInfo("send",tokenAPP,
                            idChat, LibraryClass.getUser().getId(), "Seu amigo precisa de você.!"+nomeuser);
                }
                metText.setText("");
            }
        });


    }

    @Override
    protected void onStart() {

        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try{

                            Chat model = dataSnapshot.getValue(Chat.class);
                            model.setIdMessage(dataSnapshot.getKey());
                            if (model.getTipo_message().compareTo("1") == 0) {
                                mAdapter.setFile(imageFile);
                            }

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
        mChats.clear();
        finish();
    }

    @Override
    protected void onResume() {
       super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mChats.clear();
        mFirebaseRef.unauth();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_chat, menu);

        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.m_camera) {
            int begin = (int) (1 + (Math.random() * (idChat.length()/2 - 1)));
            int begin2 = (int) (1 + (Math.random() * (idChat.length()/2 - 1)));
            int end = (int) (idChat.length()/2 + (Math.random() * (idChat.length() - idChat.length()/2)));
            int end2 = (int) (idChat.length()/2 + (Math.random() * (idChat.length() - idChat.length()/2)));

            File picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            imageFile = new File(picsDir, idChat.substring(begin, end)+(idChat.substring(begin2/2, end2/2)+mId).hashCode()+"_image.jpg");
            uri = imageFile.toString();
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            return true;

        }else if (id == R.id.m_galeria) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);

            return true;

        }else if (id == R.id.m_doc) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent,"Select a File to Upload"), SELECT_DOCUMENT);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String uriDOW;
        //preencher = true;
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                mFirebaseRef.push().setValue(new Chat(uri, nomeuser, mId, "1", " ", false, false, idChat));

                FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();

                firebaseInstanceIDService.enviaInfo("add",tokenAPP,
                        idChat, LibraryClass.getUser().getId(), "Uma nova mensagem te espera.");

                firebaseInstanceIDService.enviaInfo("send",tokenAPP,
                        idChat, LibraryClass.getUser().getId(), nomeuser +" te enviou uma foto!");
                //preencher = true;

            }
        }else  if (requestCode == SELECT_PICTURE){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPathArquivo(this,selectedImageUri);
                Toast.makeText(this, selectedImageUri.toString(), Toast.LENGTH_LONG).show();
                mFirebaseRef.push().setValue(new Chat(selectedImagePath, nomeuser, mId, "1", " ", false, false,idChat));

                FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();

                firebaseInstanceIDService.enviaInfo("add",tokenAPP,
                        idChat, LibraryClass.getUser().getId(), "Uma nova mensagem te espera.");

                firebaseInstanceIDService.enviaInfo("send",tokenAPP,
                        idChat, LibraryClass.getUser().getId(), nomeuser +" te enviou uma foto!");

            }
        }
        else  if (requestCode == SELECT_DOCUMENT){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                selectedDocumentPath = getPathArquivo(this,selectedImageUri);
                mFirebaseRef.push().setValue(new Chat(selectedDocumentPath, nomeuser, mId, "2", " ", false,false, idChat));

                FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();

                firebaseInstanceIDService.enviaInfo("add",tokenAPP,
                        idChat, LibraryClass.getUser().getId(), "Uma nova mensagem te espera.");

                firebaseInstanceIDService.enviaInfo("send",tokenAPP,
                        idChat, LibraryClass.getUser().getId(), nomeuser +" te enviou um arquivo!");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * auxiliar para saber o caminho de uma imagem URI
     */
    public String getPath(Uri uri) {

        if( uri == null ) {
            // TODO realizar algum log ou feedback do utilizador
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }


    public String getPathArquivo(Context context, Uri uri){
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = 0;
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow( "_data" );

                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                }

            } catch (Exception e) {

            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

}




