package br.com.tiradividas;

import android.app.Application;
import android.renderscript.Script;

import com.firebase.client.Firebase;

/**
 * Created by daniel on 13/06/16.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
