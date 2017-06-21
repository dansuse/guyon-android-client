package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener, UserPostFragment.OnFragmentInteractionListener{

    ImageView logut,load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        load = (ImageView) findViewById(R.id.btn_up_load);
        logut = (ImageView) findViewById(R.id.btn_logout);
        SessionManager session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn())
        {
            SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails();
            String username = user.get("email");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
            ft.replace(R.id.container, UserPostFragment.newInstance("user?username=" + username));
            ft.commit();

            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.replace(R.id.menu, MenuFragment.newInstance(2));
            ft.commit();
        }
        else
        {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        }
        logut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent up = new Intent(ProfileActivity.this, Upload.class);
                startActivity(up);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void logout()
    {
        SQLiteHandler db;
        SessionManager session;

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        session.setLogin(false);
        db.deleteUsers();
        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
