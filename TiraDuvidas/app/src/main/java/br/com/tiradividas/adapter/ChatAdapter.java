package br.com.tiradividas.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.tiradividas.Model.Chat;
import br.com.tiradividas.R;
import br.com.tiradividas.util.LibraryClass;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> mDataSet;
    private String mId;
    private File file;
    private StorageReference reference;
    private Firebase firebase;

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
        public ProgressBar progressBar;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            author = (TextView) itemView.findViewById(R.id.author);
            message = (TextView) itemView.findViewById(R.id.message);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_teste);
            imageView = (ImageView) itemView.findViewById(R.id.chat_img_right);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     * @param id      Device id
     */
    public ChatAdapter(List<Chat> dataSet, String id, Firebase firebase) {
        mDataSet = dataSet;
        mId = id;
        this.firebase = firebase;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
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
        if (mDataSet.get(position).getId().compareTo(mId) == 0 ) {

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
        else if (mDataSet.get(position).getId().compareTo(mId) != 0 ) {

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Chat chat = mDataSet.get(position);

        if (chat.getTipo_message().compareTo("0") == 0) {
            if (holder.message != null) {
                holder.message.setText(chat.getMessage());
            }
            if (holder.author != null) {
                holder.author.setText(chat.getAuthor());
            }
        }
        if (chat.getTipo_message().compareTo("1") == 0 && !chat.isEnviado()) {
            if (holder.progressBar != null && holder.imageView != null) {

                holder.progressBar.setVisibility(View.VISIBLE);
                FirebaseStorage storage = LibraryClass.getStorage();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://chatduvidas.appspot.com");


                File file1 = new File(chat.getMessage());
                Uri file = Uri.fromFile(file1);

                //holder.imageView.setImageURI(file);

                UploadTask uploadTask = storageRef.child("images/" + file.getLastPathSegment()).putFile(file);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        Snackbar.make(holder.progressBar,
                                "Imagem enviado com sucesso.",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        holder.progressBar.setVisibility(View.INVISIBLE);
                        chat.setEnviado(true);
                        Map<String, Object> map = new HashMap<>();
                        map.put("enviado", true);

                        if (downloadUrl != null) {
                            map.put("linkdow", downloadUrl.toString());
                        }
                        firebase.child(chat.getIdMessage()).updateChildren(map);

                        //Log.i("DOW", downloadUrl.toString());

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        holder.progressBar.setProgress((int) progress);
                        System.out.println("Upload is " + progress + "% done");
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Snackbar.make(holder.progressBar,
                                "Erro ao enviar imagem!",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });

            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public StorageReference getReference() {
        return reference;
    }

    public void setReference(StorageReference reference) {
        this.reference = reference;
    }
}