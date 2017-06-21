package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eko Pranotodarmo on 6/13/2017.
 */

public class KomentarAdapter  extends ArrayAdapter<Komentar> {
    private Context context;
    private ArrayList<Komentar> komentars;

    public KomentarAdapter(Context context, ArrayList<Komentar> komentars)
    {
        super(context, R.layout.row_item_comment, komentars);
        this.context = context;
        this.komentars = komentars;
    }

    @Override
    public Komentar getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Komentar komentar = getItem(position);
        if(convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_comment,parent,false);}

        TextView txtUsername = (TextView) convertView.findViewById(R.id.usernamecomment);
        final TextView txtPoin = (TextView) convertView.findViewById(R.id.poincomment);
        final TextView txtKomen = (TextView) convertView.findViewById(R.id.komentar);
        final ImageView btnUp = (ImageView) convertView.findViewById(R.id.btn_up);
        final ImageView btnDown  = (ImageView) convertView.findViewById(R.id.btn_down);

        txtUsername.setText(komentar.getUsername());
        txtPoin.setText(String.valueOf(komentar.getPoin()) + " Points");
        txtKomen.setText(String.valueOf(komentar.getKomen()));

        if(komentar.getLike() == 1)
        {
            btnUp.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
            btnDown.setImageResource(R.drawable.ic_thumb_down);
        }
        else if(komentar.getLike() == -1)
        {
            btnDown.setImageResource(R.drawable.ic_thumb_down_blue_24dp);
            btnUp.setImageResource(R.drawable.ic_thumb_up);
        }
        else
        {
            btnDown.setImageResource(R.drawable.ic_thumb_down);
            btnUp.setImageResource(R.drawable.ic_thumb_up);
        }

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(context);
                if(session.isLoggedIn()) {
                    new KomentarAdapter.UpKomen().execute(komentar.getId());
                    btnUp.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                    btnDown.setImageResource(R.drawable.ic_thumb_down);
                    if (komentar.getLike() == 1)
                    {
                        txtPoin.setText(komentar.getPoin() + " Points");
                    }
                    else if(komentar.getLike() == -1)
                    {
                        txtPoin.setText(komentar.getPoin() + 2 + " Points");
                    }
                    else
                    {
                        txtPoin.setText(komentar.getPoin() + 1 + " Points");
                    }
                }
                else
                {
                    Toast.makeText(context, "Harap Login Terlebih Dahulu", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(context);
                if(session.isLoggedIn()) {
                    new KomentarAdapter.DownKomen().execute(komentar.getId());
                    btnDown.setImageResource(R.drawable.ic_thumb_down_blue_24dp);
                    btnUp.setImageResource(R.drawable.ic_thumb_up);
                    if (komentar.getLike() == -1)
                    {
                        txtPoin.setText(komentar.getPoin() + " Points");
                    }
                    else if(komentar.getLike() == 1)
                    {
                        txtPoin.setText(komentar.getPoin() - 2 + " Points");
                    }
                    else
                    {
                        txtPoin.setText(komentar.getPoin() - 1 + " Points");
                    }
                }
                else
                {
                    Toast.makeText(context, "Harap Login Terlebih Dahulu", Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }

    private class UpKomen extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            SessionManager session = new SessionManager(context);
            if(session.isLoggedIn()) {
                SQLiteHandler db = new SQLiteHandler(context);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                String url = "http://" + HttpHandler.IP + "/guyon/api/post/upvote_comment";
                url = url.replace(" ", "%20");
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("id", params[0]);
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
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //UserPostAdapter.this.notifyDataSetChanged();
        }
    }

    private class DownKomen extends AsyncTask<String, Void, String>
    {
        private String message;
        private boolean status;

        @Override
        protected String doInBackground(String... params) {
            String TAG = UserPostFragment.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            SessionManager session = new SessionManager(context);
            if(session.isLoggedIn()) {
                SQLiteHandler db = new SQLiteHandler(context);
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                String url = "http://" + HttpHandler.IP + "/guyon/api/post/downvote_comment";
                url = url.replace(" ", "%20");
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("id", params[0]);
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
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //UserPostAdapter.this.notifyDataSetChanged();
        }
    }
}
