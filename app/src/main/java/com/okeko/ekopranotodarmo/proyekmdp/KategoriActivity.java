package com.okeko.ekopranotodarmo.proyekmdp;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class KategoriActivity extends AppCompatActivity implements UserPostFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kategori);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        ft.replace(R.id.container, UserPostFragment.newInstance("kategori/?id=" + s));
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
