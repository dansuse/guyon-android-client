package com.okeko.ekopranotodarmo.proyekmdp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class KomentarActivity extends AppCompatActivity {

    private ListView listData;
    private ArrayList<Komentar> komentars = new ArrayList<>();
    private KomentarAdapter komentarAdapter;
    private EditText edKomen;
    private Button btnSubmit;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.komentar);

        listData = (ListView) findViewById(R.id.list_data);
        komentarAdapter = new KomentarAdapter(KomentarActivity.this, komentars);
        listData.setAdapter(komentarAdapter);

        id = getIntent().getStringExtra("EXTRA_SESSION_ID");

        edKomen = (EditText) findViewById(R.id.ed_komentar);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SessionManager session = new SessionManager(KomentarActivity.this);
                if(session.isLoggedIn())
                {
                    new doKomen().execute(id,edKomen.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Login Dulu",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //komentars.add(new Komentar(id,"qwe","asd","asd","asdasd",10,10));
        //komentarAdapter.notifyDataSetChanged();
        new getKomen().execute();
    }

    private class getKomen extends AsyncTask<Void, Void, Void>
    {
        private SQLiteHandler db;
        private SessionManager session;

        private ArrayList<Komentar> tempKomen = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://"+HttpHandler.IP+"/guyon/api/post/comment?id="+id + "&access_token=" + HttpHandler.ACCESS_TOKEN;

            session = new SessionManager(KomentarActivity.this);
            if(session.isLoggedIn())
            {
                db = new SQLiteHandler(KomentarActivity.this);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                url = "http://"+HttpHandler.IP+"/guyon/api/post/comment?id="+id+"&username="+username;
                Log.e(TAG, "Response from url: " + url);
            }
            url = url.replace(" ", "%20");
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray json = new JSONArray(jsonStr);
                    tempKomen.clear();
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject e = json.getJSONObject(i);
                        String idPost = e.getString("idpost");
                        if(!idPost.equalsIgnoreCase("0")) {
                            String id = e.getString("id");
                            String idReply = e.getString("idcomment");
                            String username = e.getString("username");
                            String komen = e.getString("comment");
                            int poin = e.getInt("like_count");
                            int like = e.getInt("like");

                            tempKomen.add(new Komentar(id,idPost,idReply,username,komen,poin,like));
                        }
                    }
                }
                catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            for(int i = 0 ; i < tempKomen.size() ; i++)
            {
                komentars.add(tempKomen.get(i));
            }
            komentarAdapter.notifyDataSetChanged();
        }
    }

    private class doKomen extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            SessionManager session = new SessionManager(KomentarActivity.this);
            if(session.isLoggedIn()) {
                SQLiteHandler db = new SQLiteHandler(KomentarActivity.this);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                String url = "http://" + HttpHandler.IP + "/guyon/api/post/comment";
                url = url.replace(" ", "%20");
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("id", params[0]);
                param.put("comment", params[1]);
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
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            komentars.clear();
            new getKomen().execute();
            edKomen.setText("");
        }
    }
}
