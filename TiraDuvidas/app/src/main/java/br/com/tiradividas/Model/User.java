package br.com.tiradividas.Model;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.tiradividas.activityes.Localizacao;
import br.com.tiradividas.util.LibraryClass;

@JsonIgnoreProperties({"id", "senha"})
public class User {

    public static String PROVIDER = "PROVIDER";
    public static String TOKEN = "TOKEN";
    private static User user;

    private String nome;
    private String email;
    private String senha;
    private String escolaridade;
    private String idade;
    private String universidade_escola;
    private String curso_serie;
    private String materia_domoinio;
    private String materia_dificuldade;
    private String id;
    private String id_foto;
    private ArrayList<String> ids_post;
    private String latitude;
    private String logetude;

    public static User newUser(){
        if (user == null){
            user = new User();
        }
        return  ( user );
    }

    public User() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    private void setNameInMap( Map<String, Object> map ) {
        if( getNome() != null ){
            map.put( "nome", getNome() );
        }
    }

    public void setNameIfNull(String name) {
        if( this.nome == null ){
            this.nome = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap( Map<String, Object> map ) {
        if( getEmail() != null ){
            map.put( "email", getEmail() );
        }
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }

    }

    //@Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getUniversidade_escola() {
        return universidade_escola;
    }

    public void setUniversidade_escola(String universidade_escola) {
        this.universidade_escola = universidade_escola;
    }

    public String getCurso_serie() {
        return curso_serie;
    }

    public void setCurso_serie(String curso_serie) {
        this.curso_serie = curso_serie;
    }

    public String getMateria_domoinio() {
        return materia_domoinio;
    }

    public void setMateria_domoinio(String materia_domoinio) {
        this.materia_domoinio = materia_domoinio;
    }

    public String getMateria_dificuldade() {
        return materia_dificuldade;
    }

    public void setMateria_dificuldade(String materia_dificuldade) {
        this.materia_dificuldade = materia_dificuldade;
    }

    //@Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_foto() {
        return id_foto;
    }

    public void setId_foto(String id_foto) {
        this.id_foto = id_foto;
    }

    public ArrayList<String> getIds_post() {
        return ids_post;
    }

    public void setIds_post(ArrayList<String> ids_post) {
        this.ids_post = ids_post;
    }

    public void clearIdsPosts(){
        this.ids_post.clear();
    }

    @Override
    public String toString() {
        return "User{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", escolaridade='" + escolaridade + '\'' +
                ", idade='" + idade + '\'' +
                ", universidade_escola='" + universidade_escola + '\'' +
                ", curso_serie='" + curso_serie + '\'' +
                ", materia_domoinio='" + materia_domoinio + '\'' +
                ", materia_dificuldade='" + materia_dificuldade + '\'' +
                '}';
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLogetude() {
        return logetude;
    }

    public void setLogetude(String logetude) {
        this.logetude = logetude;
    }


    public String getTokenSP(Context context ){
        String token = LibraryClass.getSP( context, TOKEN );
        return( token );
    }

    public void saveTokenSP(Context context, String token ){
        LibraryClass.saveSP( context, TOKEN, token );
    }


    public void saveDB(){
        Firebase firebase = LibraryClass.getFirebase();
        firebase = firebase.child("users").child( getId() );
        firebase.setValue( this );
    }


}
