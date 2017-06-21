package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditPost extends AppCompatActivity {

    ImageView back;
    EditText ttle;
    Button submit,delete;
    ImageView pic;
    String id;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        ttle = (EditText) findViewById(R.id.ed_edit_title);
        submit = (Button) findViewById(R.id.submit);
        delete = (Button) findViewById(R.id.delete);
        back = (ImageView) findViewById(R.id.ed_back3);
        pic = (ImageView) findViewById(R.id.img2);

        id = getIntent().getStringExtra("EXTRA_SESSION_ID");

        try {
            pic.setImageBitmap(UserPostAdapter.gambar);
        }
        catch (Exception e){
            pic.setImageResource(R.drawable.post_loading);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ttle.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Title Kosong",Toast.LENGTH_SHORT).show();
                }
                else{
                    title = ttle.getText().toString();
                    //Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
                    new EditTask().execute();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
                new deletePost().execute();
            }
        });
    }

    private class EditTask extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://"+HttpHandler.IP+"/guyon/api/post/edit_caption?access_token=" + HttpHandler.ACCESS_TOKEN;
            url = url.replace(" ", "%20");
            HashMap<String, String> param = new HashMap<>();
            param.put("id", id);
            param.put("caption", title);
            String jsonStr = sh.makeServiceCallPost(url, param);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    message = jsonObj.getString("message");
                    status = Boolean.parseBoolean(jsonObj.getString("status"));
                    return message;
                }
                catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class deletePost extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://"+HttpHandler.IP+"/guyon/api/post/delete?access_token=" + HttpHandler.ACCESS_TOKEN;
            url = url.replace(" ", "%20");
            HashMap<String, String> param = new HashMap<>();
            param.put("id", id);
            SQLiteHandler db = new SQLiteHandler(EditPost.this);
            HashMap<String, String> user = db.getUserDetails();
            String username = user.get("email");
            param.put("username", username);
            String jsonStr = sh.makeServiceCallPost(url, param);
            //Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    message = jsonObj.getString("message");
                    status = Boolean.parseBoolean(jsonObj.getString("status"));
                    return message;
                }
                catch (final JSONException e)
                {
                    //Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            }
            else
            {
                //Log.e(TAG, "Couldn't get json from server.");
            }
            return message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
