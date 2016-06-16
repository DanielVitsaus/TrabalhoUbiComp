package br.com.tiradividas.util;

import android.util.Log;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import br.com.tiradividas.Model.User;

public class CustomChildEventListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        User u = dataSnapshot.getValue( User.class );
        Log.i("log", "ADDED");
        Log.i("log", "Name: "+u.getNome());
        Log.i("log", "Email: "+u.getEmail());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        User u = dataSnapshot.getValue( User.class );
        Log.i("log", "CHANGED");
        Log.i("log", "Name: "+u.getNome());
        Log.i("log", "Email: "+u.getEmail());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        Log.i("log", "REMOVED");
        Log.i("log", "Name: "+u.getNome());
        Log.i("log", "Email: "+u.getEmail());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(FirebaseError firebaseError) {}
}