package br.com.tiradividas.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.tiradividas.Model.User;

/**
 * Created by danielcandido on 21/06/16.
 */
public class UpdateBD extends SQLiteOpenHelper {

    private static final String TAG = "sql";
    public static final String NOME_BANCO = "user";
    private static final int VERSAO_BANCO = 1;
    private Context context;
    //private List<User> listapaciente;

    public UpdateBD(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        this.context  = context;
        //listapaciente = new ArrayList<User>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "Criando banco de dados");

        //db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL("create table if not exists \"users\" (_id integer primary key autoincrement, " +
                "_nome varchar[100], _email varchar[200], _escolaridade varchar[200], _idade varchar[10],_matdom varchar[100]," +
                "_matdif varchar[100], _idfirebase varchar[512]);");


        Log.d(TAG, "Banco criado com sucesso");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Caso mude a versao do banco de dados, podemos execultar uma sql aqui.

    }

    public long adicionaUser(User user)
    {

        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("PRAGMA foreign_keys=ON;");
        String[] columns  = new String[] {"_id","_nome"};

        try{

            Cursor c = db.query("users", columns, "_nome=?", new String[]{user.getNome()}, null, null, null);

            ContentValues valuesP = new ContentValues();
            //ContentValues valuesA = new ContentValues();
            //ContentValues valuesAtu = new ContentValues();

            if (c.getCount() <= 0) {
                valuesP.put("_nome", user.getNome());
                valuesP.put("_email", user.getEmail());
                valuesP.put("_idade", user.getIdade());
                valuesP.put("_escolaridade", user.getEscolaridade());
                valuesP.put("_matdom", user.getMateria_domoinio());
                valuesP.put("_matdif", user.getMateria_dificuldade());
                valuesP.put("_idfirebase", user.getId());

                db.insert("user", null, valuesP);

            }

        }finally {
            db.close();
        }

        return 1;
    }

    public int deleteUser(long id){

        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("PRAGMA foreign_keys=ON;");
        //String[] args = {String.valueOf(id)};

        int valor = 0;

        try{
            db.delete("users", "_idfirebase=" + id, null);
            valor = db.delete("user", "_id=" + id, null);
        }finally {
            db.close();
        }

        return valor;
    }

    public List<User> findAllUser(){

        SQLiteDatabase db = getWritableDatabase();
        List<User> users = new ArrayList<User>();

        try{

            Cursor c = db.query("users",null,null,null,null,null, "_nome");

            while (c.moveToNext()) {

                User p = new User(c.getString(c.getColumnIndex("_nome")),
                        c.getString(c.getColumnIndex("_email")), c.getString(c.getColumnIndex("_escolaridade")),
                        c.getString(c.getColumnIndex("_idade")), c.getString(c.getColumnIndex("_matdom")),
                        c.getString(c.getColumnIndex("_matdif")), c.getString(c.getColumnIndex("_idfirebase")));

                users.add(p);
            }

        }finally {
            db.close();
        }

        return users;

    }

    public User findUser(String id)
    {

        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("PRAGMA foreign_keys=ON;");
        String[] columns  = new String[] {"_id","_nome"};
        User user = new User();

        try{

            Cursor c = db.query("user", columns, "_nome=?", new String[]{id}, null, null, null);

            ContentValues valuesP = new ContentValues();

            if (c.getCount() <= 0) {
                valuesP.put("_nome", user.getNome());
                valuesP.put("_email", user.getEmail());
                valuesP.put("_idade", user.getIdade());
                valuesP.put("_escolaridade", user.getEscolaridade());
                valuesP.put("_matdom", user.getMateria_domoinio());
                valuesP.put("_matdif", user.getMateria_dificuldade());
                valuesP.put("_idfirebase", user.getId());

                db.insert("user", null, valuesP);

            }

        }finally {
            db.close();
        }

        return user;
    }

}