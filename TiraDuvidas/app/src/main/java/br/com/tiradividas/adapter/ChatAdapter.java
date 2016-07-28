package br.com.tiradividas.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
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

    private boolean receber = false;

    /**
     * Inner Class for a recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView message;
        public ProgressBar progressBar;
        public ProgressBar progressBar_dow;
        public ImageView imageView;
        public ImageView imageView_dow;
        public ImageButton imageButton;

        public ViewHolder(View v) {
            super(v);
            author = (TextView) itemView.findViewById(R.id.author);
            message = (TextView) itemView.findViewById(R.id.message);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_up);
            progressBar_dow = (ProgressBar) itemView.findViewById(R.id.progressBar_dow);
            imageView = (ImageView) itemView.findViewById(R.id.chat_img_right);
            imageView_dow = (ImageView) itemView.findViewById(R.id.image_dow);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton_dow);
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
                receber = false;
            break;

            case CHAT_DOC_RIGHT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_file_right, parent, false);
                receber = false;
            break;

            case CHAT_LEFT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_left, parent, false);
            break;

            case CHAT_IMAGE_LEFT:
                receber = true;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_message_foto_left, parent, false);
            break;

            case CHAT_DOC_LEFT:
                receber = true;
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

        if (!receber) {

            if (chat.getTipo_message().compareTo("1") == 0 && !chat.isEnviado()) {
                upImage(holder, chat);
            }

            if (chat.getTipo_message().compareTo("2") == 0 && !chat.isEnviado()) {
                upFile(holder,chat);
            }

        }else {
            if (chat.getTipo_message().compareTo("1") == 0 && !chat.isBaixado()) {
                dowImage(holder, chat);
                receber = false;
            }

            if (chat.getTipo_message().compareTo("2") == 0 && !chat.isBaixado()) {
                dowFile(holder,chat);
                receber = false;
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

    public void upImage(final ViewHolder holder, final Chat chat){

        if (holder.progressBar != null && holder.imageView != null) {

            File file1 = new File(chat.getMessage());

            if (file1 != null && file1.exists()) {

                holder.progressBar.setVisibility(View.VISIBLE);
                FirebaseStorage storage = LibraryClass.getStorage();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://chatduvidas.appspot.com");

                Uri file = Uri.fromFile(file1);
                int w = holder.imageView.getWidth();
                int h = holder.imageView.getHeight();
                    /*
                    holder.imageView.setImageURI(file);
                    BitmapDrawable drawable = (BitmapDrawable) holder.imageView.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
                    holder.imageView.setImageBitmap(bitmap);
                    */

                //holder.imageView.setImageURI(file);

                chat.setEnviado(true);
                Map<String, Object> map = new HashMap<>();
                map.put("enviado", true);
                firebase.child(chat.getIdMessage()).updateChildren(map);

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

                        chat.setEnviado(false);
                        Map<String, Object> map = new HashMap<>();
                        map.put("enviado", false);
                        firebase.child(chat.getIdMessage()).updateChildren(map);

                        Snackbar.make(holder.progressBar,
                                "Erro ao enviar imagem!",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });

            }
        }
    }

    public void upFile(final ViewHolder holder, final Chat chat){

        if (holder.progressBar != null) {

            File file1 = new File(chat.getMessage());

            if (file1 != null && file1.exists()) {

                holder.progressBar.setVisibility(View.VISIBLE);
                FirebaseStorage storage = LibraryClass.getStorage();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://chatduvidas.appspot.com");

                chat.setEnviado(true);
                Map<String, Object> map = new HashMap<>();
                map.put("enviado", true);
                firebase.child(chat.getIdMessage()).updateChildren(map);

                Uri file = Uri.fromFile(file1);

                UploadTask uploadTask = storageRef.child("archives/" + file.getLastPathSegment()).putFile(file);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        Snackbar.make(holder.progressBar,
                                "Arquivo enviado com sucesso.",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        holder.progressBar.setVisibility(View.INVISIBLE);
                        chat.setEnviado(true);
                        Map<String, Object> map = new HashMap<>();
                        map.put("enviado", true);

                        if (downloadUrl != null) {
                            map.put("linkdow", downloadUrl.toString());
                        }
                        firebase.child(chat.getIdMessage()).updateChildren(map);

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
                        chat.setEnviado(false);
                        Map<String, Object> map = new HashMap<>();
                        map.put("enviado", false);
                        firebase.child(chat.getIdMessage()).updateChildren(map);
                        Snackbar.make(holder.progressBar,
                                "Erro ao enviar imagem!",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });

            }
        }
    }

    public void dowImage(final ViewHolder holder, final Chat chat){

        if (holder.progressBar_dow != null && holder.imageButton != null){

            holder.imageView_dow.setVisibility(View.VISIBLE);

            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.imageView_dow.setVisibility(View.INVISIBLE);

                    String[] part = chat.getMessage().split("/");
                    String imageName = part[part.length-1];
                    System.out.println(imageName);

                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);

                    File localFile = null;
                    try {
                        localFile = File.createTempFile("images", "jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (file != null) {

                        holder.progressBar_dow.setVisibility(View.VISIBLE);
                        FirebaseStorage storage = LibraryClass.getStorage();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://chatduvidas.appspot.com");

                        StorageReference download = storageRef.child("images/"+imageName);
                        chat.setBaixado(true);
                        Map<String, Object> map = new HashMap<>();
                        map.put("baixado", true);
                        firebase.child(chat.getIdMessage()).updateChildren(map);

                        download.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                holder.progressBar_dow.setVisibility(View.INVISIBLE);

                                chat.setBaixado(true);
                                Map<String, Object> map = new HashMap<>();
                                map.put("baixado", true);

                                firebase.child(chat.getIdMessage()).updateChildren(map);


                                Snackbar.make(holder.progressBar_dow,
                                        "Imagem baixada com sucesso.",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                holder.progressBar_dow.setProgress((int) progress);
                                System.out.println("Upload is " + progress + "% done");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Deu Errado");
                                holder.imageView_dow.setVisibility(View.VISIBLE);
                                holder.progressBar_dow.setVisibility(View.INVISIBLE);

                                chat.setBaixado(false);
                                Map<String, Object> map = new HashMap<>();
                                map.put("baixado", false);
                                firebase.child(chat.getIdMessage()).updateChildren(map);

                                Snackbar.make(holder.progressBar_dow,
                                        "Erro ao baixar a imagem.",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            }
                        });
                    }
                }
            });
        }
    }

    public void dowFile(final ViewHolder holder, final Chat chat){

        if (holder.progressBar_dow != null && holder.imageButton != null){

            holder.imageView_dow.setVisibility(View.VISIBLE);

            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.imageView_dow.setVisibility(View.INVISIBLE);

                    String[] part = chat.getMessage().split("/");
                    String imageName = part[part.length-1];
                    System.out.println(imageName);
                    String[] tiop = imageName.split(".");
                    File file;

                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imageName);

                    File localFile = null;
                    try {
                        localFile = File.createTempFile("images", "jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (file != null) {

                        holder.progressBar_dow.setVisibility(View.VISIBLE);
                        FirebaseStorage storage = LibraryClass.getStorage();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://chatduvidas.appspot.com");

                        chat.setBaixado(true);
                        Map<String, Object> map = new HashMap<>();
                        map.put("baixado", true);
                        firebase.child(chat.getIdMessage()).updateChildren(map);

                        StorageReference download = storageRef.child("archives/"+imageName);
                        System.out.println(imageName);
                        download.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                holder.progressBar_dow.setVisibility(View.INVISIBLE);

                                chat.setBaixado(true);
                                Map<String, Object> map = new HashMap<>();
                                map.put("baixado", true);
                                firebase.child(chat.getIdMessage()).updateChildren(map);


                                Snackbar.make(holder.progressBar_dow,
                                        "Arquivo baixada com sucesso.",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                holder.progressBar_dow.setProgress((int) progress);
                                System.out.println("Upload is " + progress + "% done");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.imageView_dow.setVisibility(View.VISIBLE);
                                holder.progressBar_dow.setVisibility(View.INVISIBLE);

                                chat.setBaixado(false);
                                Map<String, Object> map = new HashMap<>();
                                map.put("baixado", false);
                                firebase.child(chat.getIdMessage()).updateChildren(map);

                                Snackbar.make(holder.progressBar_dow,
                                        "Erro ao baixar o arquivo.",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            }
                        });
                    }
                }
            });
        }
    }
}