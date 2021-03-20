package com.example.ecuapet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class perfil_mascota extends AppCompatActivity implements View.OnClickListener {
        EditText etnombrem, etedadm, etmesesm,  etcolorm, etalergiasm,  etdescripcionm;
        Spinner sprazam, spgenerom, sppesom;
        Bundle dato;
        String idMascota;
        ImageView imageProfile;
        String hostname;
        String basepath;
        ArrayAdapter<String> adapter, adapter1, adapter2;

    public static final String KEY_User_Document1 = "image";
    private String Document_img1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_mascota);

        etnombrem = findViewById(R.id.etnombreM);
        etedadm = findViewById(R.id.etedadM);
        etmesesm = findViewById(R.id.etmesesM);
        sprazam = findViewById(R.id.sprazaM);
        spgenerom = findViewById(R.id.spgeneroM);
        etcolorm = findViewById(R.id.etcolorM);
        etalergiasm = findViewById(R.id.etalergiasM);
        sppesom = findViewById(R.id.sppesoM);
        etdescripcionm = findViewById(R.id.etdescripcionM);
        hostname = getString(R.string.hostname);
        basepath = getString(R.string.basepath);

        dato = getIntent().getExtras();
        System.out.println("-----1 hola2------- : " +(dato.getString("idMascota")) );

       idMascota = dato.getString("idMascota");  //Convierto en de string a int
     // idMascota = ((MyApplication) this.getApplication()).getIdMascota();


        String[] arraySpinner = new String[] {
                "Select One",
                "Afador","American Bulldog","American Staffordshire Terrier",
                "Barbet","Beagle","Border Terrier","Boxer","Bulldog","Bull Terrier",
                "Castellano","Cairn Terrier","Chihuahua","Chihuahua","Chow Chow","Cocker",
                "Dálmata","Dóberman","French Poodle ",
                "Galgo","Golden Retriever","Gran Boyero Suizo","Gran Danés","Husky Siberiano",
                "French","Labrador Retriever","Mestiso",
                "Pastor Alemán","Pekinés","Pinscher","Pitbull","Pit bull terrier americano","Poodle","Pointer","Rottweiler",
                "San Bernardo","Schnauzer","Schipperke","Salchicha","Siberian Husky",
                "Terranova","Terrier","Yorkshire Terrier"

        };
        Spinner s = (Spinner) findViewById(R.id.sprazaM);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        String[] arraySpinner1 = new String[]{
                "Select One", "Macho", "Hembra"
        };
        Spinner s1 = (Spinner) findViewById(R.id.spgeneroM);
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter1);

        String[] arraySpinner2 = new String[]{
                "Select One", "1 a 5 Kg", "5 a 10 KG", "10 a 20 KG", "20 a 25 KG", "25 a 30 KG", "30 a 40 KG", "40 a 60 KG", "60 a 80 KG", "80 a 100 KG"
        };
        Spinner s2 = (Spinner) findViewById(R.id.sppesoM);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adapter2);




       imageProfile = (ImageView) findViewById(R.id.imageProfile);

        getperfil_mascota();

    }

    public void getperfil_mascota() {
        //UIL Del web service
        String ws = hostname.concat("/mascota/");
        ws = ws.concat(idMascota);

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
            String foto =jsnobject.getString("foto");
            // String latitud = jsnobject.getString("latitud");
            // String longitud = jsnobject.getString("longitud");


            etnombrem.setText(nombreM);
            etedadm.setText(edadM);
            etmesesm.setText(mesesM);
            int spinnerPosition = adapter.getPosition(razaM);
            sprazam.setSelection(spinnerPosition);
            int spinnerPosition1 = adapter1.getPosition(generoM);
            spgenerom.setSelection(spinnerPosition1);
            etcolorm.setText(colorM);
            etalergiasm.setText(alergiasM);
            int spinnerPosition2 = adapter2.getPosition(pesoM);
            sppesom.setSelection(spinnerPosition2);;
            etdescripcionm.setText(descripcionM);


            new DownloadImageTask((ImageView) findViewById(R.id.imageProfile)).execute(basepath+ "/" +  foto );



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
                    try{
                        Uri imageUri = FileProvider.getUriForFile(
                                perfil_mascota.this,
                                "com.example.ecuapet.provider", //(use your app signature + ".provider" )
                                f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    }catch(Error er){
                        System.out.println(er.getMessage());
                    }


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


    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    bitmap = getResizedBitmap(bitmap, 400);
                    imageProfile.setImageBitmap(bitmap);
                    BitMapToString(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                thumbnail = getResizedBitmap(thumbnail, 400);
                Log.w("path of image from gallery......******************.........", picturePath + "");
                imageProfile.setImageBitmap(thumbnail);
                BitMapToString(thumbnail);
            }
        }
    }

    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
        return Document_img1;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void ActualizarMasc(View v) {
        final ProgressDialog loading = new ProgressDialog(perfil_mascota.this);
        loading.setMessage("Espere...");
        loading.show();
        loading.setCanceledOnTouchOutside(false);
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, hostname + "/mascota/" + idMascota ,
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
                                alertDialogBuilder.setTitle("Error en el servidor");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);
                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                                alertDialogBuilder.show();

                            } else {
                                String error_msg = eventObject.getString("msg");
                                ContextThemeWrapper ctw = new ContextThemeWrapper(perfil_mascota.this, R.style.Theme_AlertDialog);
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);

                                alertDialogBuilder.setTitle("Registro");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);

                                final String idMascota = eventObject.getString("idMascota");
                                final String nombre = eventObject.getString("nombre");
//                                alertDialogBuilder.setIcon(R.drawable.doubletick);

                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent ide = new Intent(perfil_mascota.this, confirmacionGuardado.class);
                                        ide.putExtra("idMascota", idMascota);
                                        ide.putExtra("nombre", nombre);
                                        startActivity(ide);
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

                map.put("nombre", etnombrem.getText().toString());
                map.put("edad_anios", etedadm.getText().toString());
                map.put("edad_meses", etmesesm.getText().toString());
                map.put("raza", sprazam.getSelectedItem().toString());
                map.put("genero", spgenerom.getSelectedItem().toString());
                map.put("color", etcolorm.getText().toString());
                map.put("alergias", etalergiasm.getText().toString());
                map.put("peso", sppesom.getSelectedItem().toString());
                map.put("descripcion", etdescripcionm.getText().toString());
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