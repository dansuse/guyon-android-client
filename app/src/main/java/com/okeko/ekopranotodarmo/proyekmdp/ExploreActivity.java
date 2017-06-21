package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Locale;
import java.util.Random;

public class ExploreActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener{

    ListView listData;
    ArrayList<Explore> explores = new ArrayList<>();
    ExploreAdapter exploreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.menu, MenuFragment.newInstance(1));
        ft.commit();

        listData = (ListView) findViewById(R.id.list_explore);

        exploreAdapter = new ExploreAdapter(this, explores);
        listData.setAdapter(exploreAdapter);

        new getExplore().execute();

        listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), KategoriActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", explores.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //finish();
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
            String url = "http://"+HttpHandler.IP + "/guyon/api/post/explore?access_token=" + HttpHandler.ACCESS_TOKEN;
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
                        JSONArray images = e.getJSONArray("images");
                        ArrayList<Bitmap> image = new ArrayList<>();

                        for(int j = 0 ; j < images.length() ; j++)
                        {
                            URL urlImage = null;
                            Bitmap file = null;

                            try {

                                JSONObject f = images.getJSONObject(j);
                                String namaFile = f.getString("namafile");
                                namaFile = namaFile.replace("localhost",HttpHandler.IP);
                                urlImage = new URL(namaFile);

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 2;
                                file = BitmapFactory.decodeStream(urlImage.openConnection().getInputStream(), null, options);
                                image.add(file);

                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                                //Log.e(TAG, "image error: " + e1.getMessage());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                //Log.e(TAG, "image error: " + e1.getMessage());
                            }
                        }

                        explores.add(new Explore(id,nama,image));
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
            exploreAdapter.notifyDataSetChanged();
        }
    }
}
