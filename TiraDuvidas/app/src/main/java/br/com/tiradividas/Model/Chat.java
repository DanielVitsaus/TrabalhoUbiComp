package br.com.tiradividas.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"idMessage", "idChat"})
public class Chat {

    private String message;
    private String author;
    private String id;
    private String tipo_message;
    private String linkdow;
    private boolean isEnviado = true;
    private String idMessage;
    private String idChat;

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

    public Chat(String message, String author, String id, String tipo_message, String linkdow, boolean isEnviado, String idChat) {
        this.message = message;
        this.author = author;
        this.id = id;
        this.tipo_message = tipo_message;
        this.linkdow = linkdow;
        this.isEnviado = isEnviado;
        this.idChat = idChat;
    }

    public Chat(String message, String author, String id, String tipo_message, String linkdow, boolean isEnviado) {
        this.message = message;
        this.author = author;
        this.id = id;
        this.tipo_message = tipo_message;
        this.linkdow = linkdow;
        this.isEnviado = isEnviado;
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

    public boolean isEnviado() {
        return isEnviado;
    }

    public void setEnviado(boolean enviado) {
        isEnviado = enviado;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }
}