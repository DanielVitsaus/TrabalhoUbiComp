package br.com.tiradividas;

import android.app.Application;
import android.content.Context;
import android.renderscript.Script;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.util.LibraryClass;

/**
 * Created by daniel on 13/06/16.
 */
public class CustomApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Firebase.setAndroidContext(this);
        LibraryClass.getUser();
    }

    /*
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
    */
}
