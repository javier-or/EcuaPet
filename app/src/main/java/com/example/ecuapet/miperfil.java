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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class miperfil extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    EditText etnom, etdir, ettel, etcel, etmai;
    Bundle dato;
    int idUsuario;
    ImageView imageProfile;
    MapView mapa;

    String hostname;
    String basepath;

    double latitud;
    double longitud;

    public static final String KEY_User_Document1 = "image";
    private String Document_img1 = "";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miperfil);

        etnom = findViewById(R.id.etNombres);
        etdir = findViewById(R.id.etDireccion);
        ettel = findViewById(R.id.etTelefono);
        etcel = findViewById(R.id.etCelular);
        etmai = findViewById(R.id.etEmail);

        hostname = getString(R.string.hostname);
        basepath = getString(R.string.basepath);

        dato = getIntent().getExtras();
        idUsuario = ((MyApplication) this.getApplication()).getIdUsuario();

        if (idUsuario > 0) {
            new DownloadImageTask((ImageView) findViewById(R.id.imageProfile)).execute(basepath+"/"+dato.getString("foto"));
        } else {
            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage("No se envió el enlace de usuario");
            alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(miperfil.this, Principal.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialogBuilder.show();
        }

        imageProfile = (ImageView) findViewById(R.id.imageProfile);

        getMiperfil();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            String dir = jsnobject.getString("direccion");
            String tel = jsnobject.getString("telefono");
            String cel = jsnobject.getString("celular");
            latitud = Double.parseDouble(jsnobject.getString("latitud"));
            longitud = Double.parseDouble(jsnobject.getString("longitud"));

            // String latitud = jsnobject.getString("latitud");
            // String longitud = jsnobject.getString("longitud");
            etnom.setText(nom);
            etdir.setText(dir);
            ettel.setText(tel);
            etcel.setText(cel);

            System.out.println("<---------------------->");
            System.out.println(nom);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(miperfil.this);
        builder.setTitle("Agregar Foto!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tomar foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    try{
                        Uri imageUri = FileProvider.getUriForFile(
                                miperfil.this,
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

    public void SendDetail() {
        final ProgressDialog loading = new ProgressDialog(miperfil.this);
        loading.setMessage("Espere...");
        loading.show();
        loading.setCanceledOnTouchOutside(false);
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, hostname + "/user/"+idUsuario,
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
                                ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
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
                                ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);

                                alertDialogBuilder.setTitle("Registro");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setMessage(error_msg);

//                                alertDialogBuilder.setIcon(R.drawable.doubletick);
                                alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(miperfil.this, Principal.class);
                                        intent.putExtra("idUsuario", idUsuario);
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
                            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                            alertDialogBuilder.setTitle("No connection");
                            alertDialogBuilder.setMessage(" Connection time out error please try again ");
                            alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            alertDialogBuilder.show();
                        } else if (error instanceof AuthFailureError) {
                            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
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
                            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
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
                            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
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
                            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
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
                map.put("nombre",etnom.getText().toString());
                map.put("direccion",etdir.getText().toString());
                map.put("telefono",ettel.getText().toString());
                map.put("celular",etcel.getText().toString());
                map.put("latitud", String.valueOf(latitud));
                map.put("longitud", String.valueOf(longitud));
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

    public void Actualizar(View v) {
        //todo validaciones
        SendDetail();
//        if (Document_img1.equals("") || Document_img1.equals(null)) {
//            ContextThemeWrapper ctw = new ContextThemeWrapper(miperfil.this, R.style.Theme_AlertDialog);
//            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
//            alertDialogBuilder.setTitle("Imagen no puede estar vacía");
//            alertDialogBuilder.setMessage("Seleccione una imagen");
//            alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                }
//            });
//            alertDialogBuilder.show();
//            return;
//        } else {
//
//        }
    }



    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitud, longitud);
//        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación del Propietario"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,14.0f));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                // Drawing marker on the map
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(point);
                markerOptions.title(point.latitude + " : " + point.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                mMap.addMarker(markerOptions);
                // Create location object
                latitud = point.latitude;
                longitud = point.longitude;
//                Location location = new Location(point.latitude, point.longitude);
                // add location to SQLite database
//                locationsDB.insert(location);
            }
        });
    }


    public void atras(View v){
        Intent intentEnviar = new Intent(miperfil.this, Principal.class);
        startActivity(intentEnviar);
    }
}
