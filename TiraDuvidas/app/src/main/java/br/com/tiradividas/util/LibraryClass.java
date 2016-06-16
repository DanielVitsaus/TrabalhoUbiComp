package br.com.tiradividas.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;

import br.com.tiradividas.Model.User;

/**
 * Created by daniel on 13/06/16.
 */
public final class LibraryClass {
    public static String PREF = "PREF";
    private static Firebase firebase;
    private static User user;


    private LibraryClass(){}


    public static Firebase getFirebase(){
        if( firebase == null ){
            firebase = new Firebase("https://tiraduvidas.firebaseio.com");
        }

        return( firebase );
    }

    public static User getUser(){
        if (user == null){
            user = new User();
        }

        return ( user );
    }


    static public void saveSP(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    static public String getSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }

    static  public  void removeSP(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }
}
