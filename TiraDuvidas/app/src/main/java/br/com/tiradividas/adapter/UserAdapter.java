package br.com.tiradividas.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.chat.ChatActivity;
import br.com.tiradividas.util.Local;

/**
 * Created by daniel on 16/06/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> users;
    private Local local;
    private User user;
    private Context context;

    public UserAdapter(Context context, User user, List<User> users, Local local) {
        this.users = users;
        this.local = local;
        this.user = user;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.localizacao_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.pos = position;
        holder.text1.setText(users.get(position).getNome());
        holder.text2.setText("Distancia -> " +local.calculaDistancia(
                Double.valueOf(user.getLatitude()),
                Double.valueOf(user.getLongetude()),
                Double.valueOf(users.get(position).getLatitude()),
                Double.valueOf(users.get(position).getLongetude()) ) );

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean chat1 = false;
                boolean chat2 = false;
                Intent intent = new Intent(context, ChatActivity.class);
                String idChat = user.getId() +"_"+ users.get(position).getId();
                String idChat2 = users.get(position).getId() +"_"+ user.getId();

                List<String> lista = user.getLista_chat();

                if (lista.indexOf(idChat) != -1){

                    intent.putExtra("idchat", idChat);
                }
                else if (lista.indexOf(idChat2) != -1){

                    intent.putExtra("idchat", idChat2);
                }
                else {
                    user.addIdChat(idChat);
                    user.updateListaChat();

                    users.get(position).addIdChat(idChat);
                    users.get(position).updateListaChat();

                    intent.putExtra("idchat", idChat);
                }

                intent.putExtra("nome", user.getNome());
                intent.putExtra("iduser", user.getId());

                context.startActivity(intent);
                Toast.makeText(context, "Deu certo", Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        if (users != null){
            return users.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public View view;
        public TextView text1;
        public TextView text2;
        public int pos;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            text1 = (TextView) itemView.findViewById(R.id.loca_id_nome);
            text2 = (TextView) itemView.findViewById(R.id.local_id_distancia);
        }
    }
}
