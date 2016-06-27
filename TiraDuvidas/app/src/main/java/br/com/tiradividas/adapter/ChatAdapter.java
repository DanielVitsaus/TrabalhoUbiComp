package br.com.tiradividas.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.tiradividas.Model.Chat;
import br.com.tiradividas.R;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> mDataSet;
    private String mId;

    private static final int CHAT_RIGHT = 1;
    private static final int CHAT_LEFT = 2;

    /**
     * Inner Class for a recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView message;

        public ViewHolder(View v) {
            super(v);
            author = (TextView) itemView.findViewById(R.id.author);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     * @param id      Device id
     */
    public ChatAdapter(List<Chat> dataSet, String id) {
        mDataSet = dataSet;
        mId = id;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == CHAT_RIGHT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_right, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_left, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getId().equals(mId))
            return CHAT_RIGHT;

        return CHAT_LEFT;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mDataSet.get(position);
        holder.message.setText(chat.getMessage());
        holder.author.setText(chat.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}