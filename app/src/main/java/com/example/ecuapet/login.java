package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class login extends AppCompatActivity {
    EditText etEmail, etcontrase;
    TextView registrarse, olvidoPassword;
    String hostname;
    String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etcontrase = findViewById(R.id.etPassword);
        registrarse = findViewById(R.id.Registrarse);
        olvidoPassword = findViewById(R.id.olvidoPassword);
        hostname = getString(R.string.hostname);
    }

    public void getLogin(View v) {
        //UIL Del web service
        String ws = hostname.concat("/login");

        //Permisos de la aplicacion
        StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politica);

        URL url = null;
        HttpURLConnection conn;
        //Capturar excepciones
        try {
            url = new URL(ws);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("email", etEmail.getText().toString())
                    .appendQueryParameter("password", etcontrase.getText().toString());

            String query = builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            String json;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            json = response.toString();

            JSONObject jsnobject = new JSONObject(json);

            String idUsuario = jsnobject.getString("idUsuario");
            String token = jsnobject.getString("token");

            Intent ide = new Intent(login.this,Principal.class);

            ((MyApplication) this.getApplication()).setIdUsuario(Integer.parseInt(idUsuario));
            ((MyApplication) this.getApplication()).setToken(token);

            startActivity(ide);

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    public void goToRegister(View v){
        Intent i = new Intent(login.this,registro.class);
        startActivity(i);
    }
}