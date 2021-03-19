package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Principal extends AppCompatActivity {

    Bundle dato;
    TextView nombre;
    int idUsuario;
    String basepath;
    String hostname;
    String foto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        dato = getIntent().getExtras();
        basepath = getString(R.string.basepath);

        nombre = findViewById(R.id.nombre);
        hostname = getString(R.string.hostname);
        foto = "img/profile.jpg";

        idUsuario = ((MyApplication) this.getApplication()).getIdUsuario();
        getMiperfil();
    }

    public void perfil(View v) {

        Intent intentEnviar = new Intent(Principal.this, miperfil.class);
        intentEnviar.putExtra("foto",foto);
        startActivity(intentEnviar);

    }


    public void macotas(View v) {

        Intent intentEnviar = new Intent(Principal.this, listadoMascota.class);
        startActivity(intentEnviar);
    }

    public void agregarMascota(View v) {

        Intent intentEnviar = new Intent(Principal.this, agregarMascota.class);
        startActivity(intentEnviar);
    }

    public void getMiperfil() {
        //UIL Del web service
        String ws = hostname.concat("/user/");
        ws = ws.concat(Integer.toString(idUsuario));

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
            conn.setRequestMethod("GET");
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

            String nom = jsnobject.getString("name");
            foto = jsnobject.getString("photo");
            nombre.setText(nom);

            Log.d("=============",basepath+"/img/"+foto);

            if(foto != null){
                new DownloadImageTask((ImageView) findViewById(R.id.photoProfile)).execute(basepath+"/"+foto);
            }

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}
