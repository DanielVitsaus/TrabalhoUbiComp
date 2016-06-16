package br.com.tiradividas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.tiradividas.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public View view;
    public TextView text1;
    public TextView text2;

    public UserViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        text1 = (TextView) itemView.findViewById(R.id.loca_id_nome);
        text2 = (TextView) itemView.findViewById(R.id.local_id_distancia);
    }
}