package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class perfil_mascota extends AppCompatActivity implements View.OnClickListener {
        EditText etnombrem, etedadm, etmesesm, etrazam, etgenerom, etcolorm, etalergiasm, etpesom, etdescripcionm;
        Bundle dato;
        int idUsuario;
        ImageView imageProfile;


        String hostname;
        String basepath;

    public static final String KEY_User_Document1 = "image";
    private String Document_img1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mascota);

        etnombrem = findViewById(R.id.etNombreM);
        etedadm = findViewById(R.id.etEdadM);
        etmesesm = findViewById(R.id.etMesesM);
        etrazam = findViewById(R.id.etRazaM);
        etgenerom = findViewById(R.id.etGeneroM);
        etcolorm = findViewById(R.id.etColorM);
        etalergiasm = findViewById(R.id.etAlergiasM);
        etpesom = findViewById(R.id.etPezoM);
        etdescripcionm = findViewById(R.id.etDescripcionM);
        hostname = getString(R.string.hostname);
        basepath = getString(R.string.basepath);

        dato = getIntent().getExtras();
        idUsuario = ((MyApplication) this.getApplication()).getIdUsuario();

        if (idUsuario > 0) {
            new DownloadImageTask((ImageView) findViewById(R.id.imageProfile)).execute(basepath+"/"+dato.getString("foto"));
        } else {
            ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage("No se envió el enlace de usuario");
            alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(perfil_mascota.this, Principal.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialogBuilder.show();
        }

        imageProfile = (ImageView) findViewById(R.id.imageProfile);

        getperfil_mascota();

    }

    public void getperfil_mascota() {
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

            String nombreM = jsnobject.getString("nombreM");
            String edadM = jsnobject.getString("edad");
            String mesesM = jsnobject.getString("meses");
            String razaM = jsnobject.getString("raza");
            String generoM = jsnobject.getString("genero");
            String colorM = jsnobject.getString("color");
            String alergiasM = jsnobject.getString("alergias");
            String pesoM = jsnobject.getString("peso");
            String descripcionM = jsnobject.getString("descripcion");
            // String latitud = jsnobject.getString("latitud");
            // String longitud = jsnobject.getString("longitud");
            etnombrem.setText(nombreM);
            etedadm.setText(edadM);
            etmesesm.setText(mesesM);
            etrazam.setText(razaM);
            etgenerom.setText(generoM);
            etcolorm.setText(colorM);
            etalergiasm.setText(alergiasM);
            etpesom.setText(pesoM);
            etdescripcionm.setText(descripcionM);
            System.out.println("<---------------------->");
            System.out.println(nombreM);

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }





public void atras(View v){
        Intent intentEnviar = new Intent(perfil_mascota.this, agregarMascota.class);
        startActivity(intentEnviar);
        }

    @Override
    public void onClick(View v) {

    }
}