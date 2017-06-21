package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{


    EditText txtusername,txtpass;
    Button btnlogin;
    private SQLiteHandler db;
    private SessionManager session;
    String username;
    String pass;
    TextView gotoreg;

    private String TAG = UserPostFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //session = new SessionManager(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.menu, MenuFragment.newInstance(2));
        ft.commit();

        gotoreg = (TextView) findViewById(R.id.ed_login_gotoregis);
        txtusername = (EditText) findViewById(R.id.ed_login_username);
        txtpass = (EditText) findViewById(R.id.ed_login_pass);
        btnlogin = (Button) findViewById(R.id.ed_btn_login);

        gotoreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regis = new Intent(LoginActivity.this , NewRegister.class);
                startActivity(regis);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtusername.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Username belum terisi", Toast.LENGTH_SHORT).show();
                }
                if(txtpass.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Password belum terisi", Toast.LENGTH_SHORT).show();
                }
                else{

                    username = txtusername.getText().toString();
                    pass = txtpass.getText().toString();
                    Toast.makeText(getApplicationContext(),username + "," + pass, Toast.LENGTH_SHORT).show();
                    new LoginTask().execute(txtusername.getText().toString(), txtpass.getText().toString());
                    //Intent upload = new Intent(activity_log.this , com.example.angelo.testing.upload.class);
                    //startActivity(upload);
                }
            }
        });

        isLogin();
    }

    private class LoginTask extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://" + HttpHandler.IP + "/guyon/api/user/login";
            url = url.replace(" ", "%20");
            HashMap<String, String> param = new HashMap<>();
            param.put("user", username);
            param.put("pass", pass);
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
            return jsonStr;
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
                session.setLogin(true);
                db.addUser(username);
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void isLogin()
    {
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        }
    }
}