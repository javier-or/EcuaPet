package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

public class registro extends AppCompatActivity {
    EditText etNombres, etDireccion, etTelefono, etCelular, etEmail, etContraseña, etConfirmarContraseña;
    String hostname;

    private GpsTracker gpsTracker;
    String tvLatitude,tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        etNombres = findViewById(R.id.etNombres);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etCelular = findViewById(R.id.etCelular);
        etEmail = findViewById(R.id.etEmail);
        etContraseña = findViewById(R.id.etContraseña);
        etConfirmarContraseña = findViewById(R.id.etConfirmarContraseña);
        hostname = getString(R.string.hostname);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void Registrar(View v) {

        //UIL Del web service
        String ws = hostname.concat("/register");

        //Permisos de la aplicacion
        StrictMode.ThreadPolicy politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politica);

        URL url = null;
        HttpURLConnection conn;
        //Capturar excepciones
        try {
            getLocation();
            url = new URL(ws);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", etNombres.getText().toString())
                    .appendQueryParameter("email", etEmail.getText().toString())
                    .appendQueryParameter("password", etContraseña.getText().toString())
                    .appendQueryParameter("direccion", etDireccion.getText().toString())
                    .appendQueryParameter("telefono", etTelefono.getText().toString())
                    .appendQueryParameter("celular", etTelefono.getText().toString())
                    .appendQueryParameter("latitud", tvLatitude)
                    .appendQueryParameter("longitud", tvLongitude);

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

            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonResult = (JSONObject) jsonObject.get("result");
            JSONObject jsonUsuario = (JSONObject) jsonObject.get("user");

            String idUsuario = jsonUsuario.getString("id");

            Toast.makeText(getApplicationContext(),jsonResult.getString("message") , Toast.LENGTH_LONG).show();

            Intent ide = new Intent(registro.this, login.class);
            ide.putExtra("idUsuario", idUsuario);
            startActivity(ide);

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(registro.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            tvLatitude = String.valueOf(latitude);
            tvLongitude = String.valueOf(longitude);
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    public void volver(View v) {
        Intent intentEnviar = new Intent(registro.this, login.class);
        startActivity(intentEnviar);
    }

}
