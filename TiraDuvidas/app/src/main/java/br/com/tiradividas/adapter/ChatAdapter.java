package br.com.tiradividas.adapter;

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
    private int viewTypeee;

    private static final int CHAT_RIGHT = 1;
    private static final int CHAT_IMAGE_RIGHT = 2;
    private static final int CHAT_DOC_RIGHT = 3;
    private static final int CHAT_LEFT = 4;
    private static final int CHAT_IMAGE_LEFT = 5;
    private static final int CHAT_DOC_LEFT = 6;

    /**
     * Inner Class for a recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView message;

        public ViewHolder(View v) {
            super(v);
            if (viewTypeee == CHAT_LEFT || viewTypeee == CHAT_RIGHT) {
                author = (TextView) itemView.findViewById(R.id.author);
                message = (TextView) itemView.findViewById(R.id.message);
            }
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
        viewTypeee = viewType;
        switch (viewType){
            case CHAT_RIGHT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_right, parent, false);
            break;

            case CHAT_IMAGE_RIGHT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_foto_right, parent, false);
            break;

            case CHAT_DOC_RIGHT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_file_right, parent, false);
            break;

            case CHAT_LEFT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_left, parent, false);
            break;

            case CHAT_IMAGE_LEFT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_foto_left, parent, false);
            break;

            case CHAT_DOC_LEFT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_file_left, parent, false);
            break;

            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_right, parent, false);
            break;
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getId().equals(mId)) {
            if(mDataSet.get(position).getTipo_message().compareTo("0") == 0) {
                return CHAT_RIGHT;
            }
            if(mDataSet.get(position).getTipo_message().compareTo("1") == 0) {
                return CHAT_IMAGE_RIGHT;
            }
            if(mDataSet.get(position).getTipo_message().compareTo("2") == 0) {
                return CHAT_DOC_RIGHT;
            }

        }
        else{
            if(mDataSet.get(position).getTipo_message().compareTo("1") == 0) {
                return CHAT_IMAGE_LEFT;
            }
            if(mDataSet.get(position).getTipo_message().compareTo("2") == 0) {
                return CHAT_DOC_LEFT;
            }
        }

        return CHAT_LEFT;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mDataSet.get(position);
        if (viewTypeee == CHAT_LEFT || viewTypeee == CHAT_RIGHT) {
            holder.message.setText(chat.getMessage());
            holder.author.setText(chat.getAuthor());
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}