package com.okeko.ekopranotodarmo.proyekmdp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

public class Upload extends AppCompatActivity {
    private static final char PICK_IMAGE = ' ';
    EditText tt;
    ImageView back,gambarnya;
    Spinner combo1;
    Button open,upload;
    ArrayList<String> idKategori = new ArrayList<>();
    ArrayList<String> kategoris = new ArrayList<>();
    String kategori,title;
    Bitmap namagambar;
    ImageView imageView;

    private static final int STORAGE_PERMISSION_CODE = 123;

    String namafotopengganti = "";
    String namafilefoto = "";

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        requestStoragePermission();

        back = (ImageView) findViewById(R.id.back1);
        combo1 =(Spinner) findViewById(R.id.category);
        open = (Button) findViewById(R.id.ed_btn_galery);
        upload = (Button) findViewById(R.id.ed_btn_upload);
        gambarnya = (ImageView) findViewById(R.id.picture);
        tt = (EditText) findViewById(R.id.ed_upload_title);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tt.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Title belum terisi", Toast.LENGTH_SHORT).show();
                }else
                if(gambarnya.getDrawable() == null){
                    Toast.makeText(getApplicationContext(),"Pilih Gambar dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    title = tt.getText().toString();
                    int index = combo1.getSelectedItemPosition();
                    kategori = idKategori.get(index);
                    //Toast.makeText(getApplicationContext(),picturePath + " - " + kategori,Toast.LENGTH_SHORT).show();
                    //new doUpload().execute();
                    //uploadMultipart();
                    uploadPhoto();
                }
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new getExplore().execute();
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }



    public void uploadMultipart() {
        //getting the actual path of the image
        String path = getPath(filePath);
        //Uploading code

        SQLiteHandler db = new SQLiteHandler(Upload.this);
        HashMap<String, String> user = db.getUserDetails();
        String username = user.get("email");
        String url = "http://" + HttpHandler.IP + "/guyon/api/post/upload";
        username = "eko";
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, url)
                    .addFileToUpload(path, "userfile") //Adding file
                    .addParameter("username", username) //Adding text parameter to the request
                    .addParameter("idkategori", kategori) //Adding text parameter to the request
                    .addParameter("caption", title) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = this.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            namafilefoto = imgPath;
            //ImageView imgView  = (ImageView) findViewById(R.id.imgViewFoto);
            gambarnya.setImageURI(selectedImage);
            String namafototemp = imgPath;
            String[] arr = namafototemp.split("/");
            int panj = arr.length;
            namafotopengganti = arr[panj - 1];
            //CLoginMember.fototerpilih = namafilefoto;
            Toast.makeText(this,"masuk gallery activityresult = " + imgPath,Toast.LENGTH_LONG).show();
        }
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private class getExplore extends AsyncTask<Void, Void, Void>
    {
        private SQLiteHandler db;
        private SessionManager session;

        private ArrayList<UserPost> tempUserPost = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://"+HttpHandler.IP+"/guyon/api/post/explore?access_token=" + HttpHandler.ACCESS_TOKEN;
            url = url.replace(" ", "%20");
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url:  " + jsonStr);
            if (jsonStr != null) {
                try {

                    JSONArray json = new JSONArray(jsonStr);
                    tempUserPost.clear();
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject e = json.getJSONObject(i);
                        String id = e.getString("id");
                        String nama = e.getString("nama");
                        idKategori.add(id);
                        kategoris.add(nama);
                    }
                }
                catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());


                }

            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            List<String> kategori = new ArrayList<>();
            for(int i= 0 ; i < kategoris.size() ; i++)
            {
                kategori.add(kategoris.get(i));
            }
            ArrayAdapter adp1=new ArrayAdapter<String>(Upload.this,android.R.layout.simple_list_item_1,kategori);
            combo1.setAdapter(adp1);
        }
    }

    public void uploadPhoto() {
        final String urlString = "http://" + HttpHandler.IP +"/guyon/api/post/upload?access_token=" + HttpHandler.ACCESS_TOKEN;
        new AsyncTask<String, Void, String>() {
            URL connectURL;
            FileInputStream fileInputStream;

            protected void onPreExecute() {
                Log.e("waktu mau upload photo",namafilefoto);
                try{
                    connectURL = new URL(urlString);
                    fileInputStream = new FileInputStream(namafilefoto);
                    Log.e("httypmyupload", "constructor done ");
                }catch(Exception ex){
                    Log.i("HttpFileUpload", "URL Malformatted");
                }
            };

            @Override
            protected String doInBackground(String... params) {

                SQLiteHandler db = new SQLiteHandler(Upload.this);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                // INI WAJIB SEMUA
                DataOutputStream dos;
                String iFileName = namafilefoto;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                String Tag="fSnd";
                try
                {
                    Log.e(Tag,"Starting Http File Sending to URL");

                    // Open a HTTP connection to the URL
                    HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

                    // Allow Inputs
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("X-API-KEY", HttpHandler.API_KEY);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    dos = new DataOutputStream(conn.getOutputStream()); // buka modul pengiriman ( mobil data )

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"caption\""+ lineEnd); // kalau di html kayak gini
                    dos.writeBytes(lineEnd);                                                   // <input type="text" name="title">
                    dos.writeBytes(title);
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"idkategori\""+ lineEnd);
                    // <input type="text" name="description">
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(kategori);
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"username\""+ lineEnd);
                    // <input type="text" name="description">
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(username);
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\"" + iFileName +"\"" + lineEnd);
                    // <input type="file" name="uploadedfile">
                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size

                    int bytesAvailable = fileInputStream.available();
                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0)
                    {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    // close streams
                    fileInputStream.close();

                    dos.flush();

                    Log.e(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));

                    InputStream is = conn.getInputStream();

                    // retrieve the response from server
                    int ch;

                    StringBuffer b =new StringBuffer();
                    while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
                    String s=b.toString();
                    Log.i("Response",s);
                    dos.close();
                }
                catch (MalformedURLException ex)
                {
                    Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                }
                catch (IOException ioe)
                {
                    Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
                }

                return "sukses";
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getBaseContext(),"isi msg = " + msg,Toast.LENGTH_SHORT).show();
                //tvstatus.setText("masuk post execute");
            }
        }.execute(namafilefoto);
    }

    private class doUpload extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            SessionManager session = new SessionManager(Upload.this);
            if(session.isLoggedIn()) {
                SQLiteHandler db = new SQLiteHandler(Upload.this);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                String url = "http://" + HttpHandler.IP + "/guyon/api/user/register";
                url = url.replace(" ", "%20");
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("idkategori", kategori);
                param.put("caption", title);
                //param.put("userfile", encodedString);
                String jsonStr = sh.makeServiceCallPost(url, param);
                Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {

                        JSONObject jsonObj = new JSONObject(jsonStr);
                        message = jsonObj.getString("message");
                        status = Boolean.parseBoolean(jsonObj.getString("status"));
                        return message;
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }

                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            if(status)
            {
                finish();
            }
        }
    }
}
