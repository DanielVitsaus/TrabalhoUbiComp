package br.com.tiradividas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;

public class UserRecyclerAdapter extends FirebaseRecyclerAdapter<User, UserViewHolder> {

    public UserRecyclerAdapter(
            Class<User> modelClass,
            int modelLayout,
            Class<UserViewHolder> viewHolderClass,
            Query ref ){

        super( modelClass, modelLayout, viewHolderClass, ref );
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        android.view.View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.localizacao_item, parent, false);
        //return super.onCreateViewHolder(parent, viewType);
        return new UserViewHolder(view);
    }

    @Override
    protected void populateViewHolder(UserViewHolder userViewHolder,User user, int i) {

        //userViewHolder.text1.setText( user.getNome() );
        //userViewHolder.text2.setText( user.getEmail() );
    }

    @Override public void onBindViewHolder(final UserViewHolder holder, int position) {
        User item = getItem(position);
        holder.text1.setText(item.getNome());
        holder.text2.setText(String.valueOf(item.getEmail()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.view.getContext(), "Deu certo", Toast.LENGTH_LONG).show();
            }
        });
    }
}