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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewRegister extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{

    EditText txtusername,txtpass,txtemail,txtname;
    Button btnregis;
    TextView backto;
    String username,pass,nama,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_register);

        txtname = (EditText) findViewById(R.id.ed_register_name);
        txtusername = (EditText) findViewById(R.id.ed_register_username);
        txtpass = (EditText) findViewById(R.id.ed_register_pass);
        txtemail = (EditText) findViewById(R.id.ed_register_email);
        btnregis = (Button) findViewById(R.id.ed_btn_Register);
        backto = (TextView) findViewById(R.id.ed_register_backtologin);



        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.menu, MenuFragment.newInstance(2));
        ft.commit();

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtname.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Nama belum terisi", Toast.LENGTH_SHORT).show();
                }
                if(txtusername.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Username belum terisi", Toast.LENGTH_SHORT).show();
                }
                if(txtpass.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Password belum terisi", Toast.LENGTH_SHORT).show();
                }
                if(txtemail.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Email belum terisi", Toast.LENGTH_SHORT).show();
                }
                else {
                    String asd = txtemail.getText().toString();
                    if(asd.contains("@")){
                        if(asd.contains(".com")){
                            //Intent login = new Intent(NewRegister.this, LoginActivity.class);
                            //startActivity(login);
                            username = txtusername.getText().toString();
                            pass = txtpass.getText().toString();
                            email = txtemail.getText().toString();
                            nama = txtname.getText().toString();

                             new RegisterTask().execute();

                        }
                        else
                            Toast.makeText(getApplicationContext(),"Email invalid", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(getApplicationContext(),"Email invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class RegisterTask extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://"+HttpHandler.IP+"/guyon/api/user/register";
            url = url.replace(" ", "%20");
            HashMap<String, String> param = new HashMap<>();
            param.put("user", username);
            param.put("pass", pass);
            param.put("nama", nama);
            param.put("email", email);
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
                finish();
            }
        }
    }
}
