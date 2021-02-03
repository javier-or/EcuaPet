package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class perfil_mascota extends AppCompatActivity implements View.OnClickListener {
        EditText etnombrem, etedadm, etmesesm, etrazam, etgenerom, etcolorm, etalergiasm, etpesom, etdescripcionm;
        Bundle dato;
        int idMascota;
        ImageView imageProfile;
        String hostname;
        String basepath;

    public static final String KEY_User_Document1 = "image";
    private String Document_img1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mascota);

        etnombrem = findViewById(R.id.etnombreM);
        etedadm = findViewById(R.id.etedadM);
        etmesesm = findViewById(R.id.etmesesM);
        etrazam = findViewById(R.id.etrazaM);
        etgenerom = findViewById(R.id.etgeneroM);
        etcolorm = findViewById(R.id.etcolorM);
        etalergiasm = findViewById(R.id.etalergiasM);
        etpesom = findViewById(R.id.etpezoM);
        etdescripcionm = findViewById(R.id.etdescripcionM);
        hostname = getString(R.string.hostname);
        basepath = getString(R.string.basepath);

        dato = getIntent().getExtras();
        System.out.println("-----1 hola2------- : " +(dato.getString("idMascota")) );

       idMascota = Integer.parseInt(dato.getString("idMascota"));  //Convierto en de string a int
     // idMascota = ((MyApplication) this.getApplication()).getIdMascota();

        System.out.println("-----1 hola1------- : " + idMascota);


      


     /*   if (idMascota > 0) {
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

       imageProfile = (ImageView) findViewById(R.id.imageProfile);*/

        getperfil_mascota();

    }

    public void getperfil_mascota() {
        //UIL Del web service
        String ws = hostname.concat("/mascota/");
        ws = ws.concat(Integer.toString(idMascota));

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

            String nombreM = jsnobject.getString("nombre");
            String edadM = jsnobject.getString("edad_anios");
            String mesesM = jsnobject.getString("edad_meses");
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


    public void selectImage(View v) {
        final CharSequence[] options = {"Tomar foto", "Escoger de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(perfil_mascota.this);
        builder.setTitle("Agregar Foto!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tomar foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Escoger de Galería")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void SendDetail() {
        final ProgressDialog loading = new ProgressDialog(perfil_mascota.this);
        loading.setMessage("Espere...");
        loading.show();
        loading.setCanceledOnTouchOutside(false);
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, hostname + "/user/"+idMascota,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            loading.dismiss();
                            Log.d("JSON", response);

                            JSONObject eventObject = new JSONObject(response);
                            String error_status = eventObject.getString("error");

                            if (error_status.equals("true")) {
                                String error_msg = eventObject.getString("msg");
                                ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                                alertDialogBuilder.setTitle("Vendor Detail");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);
                                alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                                alertDialogBuilder.show();

                            } else {
                                String error_msg = eventObject.getString("msg");
                                final String nombre = eventObject.getString("nombre");
                                ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);

                                alertDialogBuilder.setTitle("Registro");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);

//                                alertDialogBuilder.setIcon(R.drawable.doubletick);
                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(perfil_mascota.this, Principal.class);
                                        intent.putExtra("idUsuario", idMascota);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                alertDialogBuilder.show();
                            }
                        } catch (Exception e) {
                            Log.d("Tag", e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("No connection");
                            alertDialogBuilder.setMessage(" Connection time out error please try again ");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                        } else if (error instanceof AuthFailureError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Connection Error");
                            alertDialogBuilder.setMessage(" Authentication failure connection error please try again ");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                            //TODO
                        } else if (error instanceof ServerError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Connection Error");
                            alertDialogBuilder.setMessage("Connection error please try again");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                            //TODO
                        } else if (error instanceof NetworkError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Connection Error");
                            alertDialogBuilder.setMessage("Network connection error please try again");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                            //TODO
                        } else if (error instanceof ParseError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("Error");
                            alertDialogBuilder.setMessage("Parse error");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                        }
//                        Toast.makeText(Login_Activity.this,error.toString(), Toast.LENGTH_LONG ).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("nombre",etnombrem.getText().toString());
                map.put("edad",etedadm.getText().toString());
                map.put("meses",etmesesm.getText().toString());
                map.put("raza",etrazam.getText().toString());
                map.put("genero",etgenerom.getText().toString());
                map.put("color",etcolorm.getText().toString());
                map.put("alergias",etalergiasm.getText().toString());
                map.put("peso",etpesom.getText().toString());
                map.put("descripcion",etdescripcionm.getText().toString());
                map.put(KEY_User_Document1, Document_img1);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(mRetryPolicy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {

    }



    public void atras(View v){
        Intent intentEnviar = new Intent(perfil_mascota.this, Principal.class);
        startActivity(intentEnviar);
        }


}