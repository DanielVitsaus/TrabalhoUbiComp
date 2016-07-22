package br.com.tiradividas.Model;

public class Chat {

    private String message;
    private String author;
    private String id;
    private String tipo_message;
    private String linkdow;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public Chat() {
    }

    public Chat(String message, String author, String id, String tipo_message) {
        this.message = message;
        this.author = author;
        this.id = id;
        this.tipo_message = tipo_message;
    }

    public Chat(String message, String author, String id, String tipo_message, String linkdow) {
        this.message = message;
        this.author = author;
        this.id = id;
        this.tipo_message = tipo_message;
        this.linkdow = linkdow;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo_message() {
        return tipo_message;
    }

    public void setTipo_message(String tipo_message) {
        this.tipo_message = tipo_message;
    }

    public String getLinkdow() {
        return linkdow;
    }

    public void setLinkdow(String linkdow) {
        this.linkdow = linkdow;
    }
}