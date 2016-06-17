package br.com.tiradividas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.tiradividas.Model.User;
import br.com.tiradividas.R;
import br.com.tiradividas.util.Local;

/**
 * Created by daniel on 16/06/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> users;
    private Local local;

    public UserAdapter(List<User> users, Local local) {
        this.users = users;
        this.local = local;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.localizacao_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //User item = users.get(position);
        //local.
        holder.text1.setText(users.get(position).getNome());
        //holder.text2.setText(String.valueOf(users.get(position).getEmail()));
        holder.text2.setText("Distancia -> " +local.calculaDistancia(
                local.getLatitude(), local.getLogetude(),
                Double.valueOf(users.get(position).getLatitude()),
                Double.valueOf(users.get(position).getLogetude()) ) );

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.view.getContext(), "Deu certo", Toast.LENGTH_LONG).show();
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

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            text1 = (TextView) itemView.findViewById(R.id.loca_id_nome);
            text2 = (TextView) itemView.findViewById(R.id.local_id_distancia);
        }
    }
}
