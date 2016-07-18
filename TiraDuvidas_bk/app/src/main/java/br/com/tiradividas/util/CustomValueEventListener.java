package br.com.tiradividas.util;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.com.tiradividas.Model.User;

/**
 * Created by daniel on 15/06/16.
 */
public class CustomValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = LibraryClass.getUser();

        for( DataSnapshot d : dataSnapshot.getChildren() ){
            User u = d.getValue( User.class );
            u.setId(d.getKey());
            if (user.getId().compareTo(u.getId()) == 0) {
                Log.i("log", u.getId());
                Log.i("log", u.toString());
                Log.i("log", user.toString());
                user = u;
                Log.i("log", user.toString());
                break;
            }
        }

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}
}