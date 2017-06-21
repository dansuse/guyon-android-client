package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Intent;
import android.graphics.Bitmap;
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

public class Report extends AppCompatActivity {

    Button report;
    EditText txt;
    ImageView back,gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        report = (Button) findViewById(R.id.btn_report);
        txt = (EditText) findViewById(R.id.ed_txtreport);
        back = (ImageView) findViewById(R.id.ed_back2);
        gambar = (ImageView) findViewById(R.id.img);
        final String id = getIntent().getStringExtra("EXTRA_SESSION_ID");
        try {


            gambar.setImageBitmap(UserPostAdapter.gambar);

        }
        catch (Exception e){
            gambar.setImageResource(R.drawable.post_loading);
        }
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Isi Alasan",Toast.LENGTH_SHORT).show();
                }
                else{
                    SessionManager session = new SessionManager(Report.this);
                    if(session.isLoggedIn()) {
                        new doReport().execute(id, txt.getText().toString());
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Login Dulu",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class doReport extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            SessionManager session = new SessionManager(Report.this);
            if(session.isLoggedIn()) {
                SQLiteHandler db = new SQLiteHandler(Report.this);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                String url = "http://" + HttpHandler.IP + "/guyon/api/post/report?access_token=" + HttpHandler.ACCESS_TOKEN;
                url = url.replace(" ", "%20");
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("id", params[0]);
                param.put("report", params[1]);
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
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Login Terlebih Dahulu",
                        Toast.LENGTH_LONG).show();
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
