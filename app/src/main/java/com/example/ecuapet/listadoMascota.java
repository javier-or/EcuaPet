package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;

public class listadoMascota extends AppCompatActivity {
    ListView list;
    ArrayList<String> titles = new ArrayList<>();
    int idUsuario;
    String hostname;
    ArrayAdapter  arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_mascota);
       arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);
        list = findViewById(R.id.list);
        list.setAdapter(arrayAdapter);

        hostname = getString(R.string.hostname);
        idUsuario = ((MyApplication) this.getApplication()).getIdUsuario();
        getListaMascota();

    }


    public void getListaMascota() {
        //UIL Del web service
        String ws = hostname.concat("/mascota?id=");
        ws=ws.concat(Integer.toString(idUsuario)); //concatena y forma el estring

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
//array

            JSONArray jsonArr=null;

            jsonArr = new JSONArray(json);

            String nombre;
            for (int i = 0; i<jsonArr.length();i++){
                nombre=""; //limpia nueva inserccion
                JSONObject objeto = jsonArr.getJSONObject(i);
                nombre +=""+i+" "+objeto.optString("nombre");
                // nombre +=objeto.optString("nombre"+"\n");
                System.out.println("<---------------------------->");
                System.out.println(nombre);
                //agregado dentro del array
                titles.add(nombre);


            }

            arrayAdapter.notifyDataSetChanged();



        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

}
