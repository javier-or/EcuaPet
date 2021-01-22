package com.example.ecuapet;

import android.app.Application;
/**
 * this class is used as globals
 * */
public class MyApplication extends Application {
    private int idUsuario;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
