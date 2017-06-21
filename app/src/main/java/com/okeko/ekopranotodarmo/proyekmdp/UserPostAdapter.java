package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by Eko Pranotodarmo on 5/12/2017.
 */

public class UserPostAdapter extends ArrayAdapter<UserPost> {
    private Context context;
    private ArrayList<UserPost> userPosts;
    public static Bitmap gambar;
    public UserPostAdapter(Context context, ArrayList<UserPost> userPosts)
    {
        super(context, R.layout.row_item_user_post, userPosts);
        this.context = context;
        this.userPosts = userPosts;
    }

    @Override
    public UserPost getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final UserPost userPost = getItem(position);
        if(convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_user_post,parent,false);}

        TextView txtPostTitle = (TextView) convertView.findViewById(R.id.txt_post_title);
        final TextView txtPostPoint = (TextView) convertView.findViewById(R.id.txt_post_point);
        TextView txtPostComment = (TextView) convertView.findViewById(R.id.txt_post_comment);
        ImageView imageViewPost = (ImageView) convertView.findViewById(R.id.image_view_post);
        final ImageView btnUp = (ImageView) convertView.findViewById(R.id.btn_up_vote);
        final ImageView btnDown = (ImageView) convertView.findViewById(R.id.btn_down_vote);
        ImageView btnMessage = (ImageView) convertView.findViewById(R.id.btn_comment);
        ImageView btnReport  = (ImageView) convertView.findViewById(R.id.btn_report);;

        txtPostTitle.setText(userPost.getCaption());
        txtPostPoint.setText(userPost.getLike_count() + " Points");
        txtPostComment.setText(userPost.getComment_count() + " Comments");

        txtPostTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(context);
                if(session.isLoggedIn()) {
                    SQLiteHandler db = new SQLiteHandler(context);
                    HashMap<String, String> user = db.getUserDetails();
                    String username = user.get("email");
                    if(userPost.getUsername().equalsIgnoreCase(username))
                    {
                        gambar = userPost.getFile();
                        Intent edit = new Intent(context,EditPost.class);
                        edit.putExtra("EXTRA_SESSION_ID", userPost.getId());
                        context.startActivity(edit);
                    }
                }
            }
        });

        if(userPost.getLike() == 1)
        {
            btnUp.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
            btnDown.setImageResource(R.drawable.ic_thumb_down);
        }
        else if(userPost.getLike() == -1)
        {
            btnDown.setImageResource(R.drawable.ic_thumb_down_blue_24dp);
            btnUp.setImageResource(R.drawable.ic_thumb_up);
        }
        else
        {
            btnDown.setImageResource(R.drawable.ic_thumb_down);
            btnUp.setImageResource(R.drawable.ic_thumb_up);
        }

        try
        {
            if(userPost.getFile() != null)
            {
                imageViewPost.setImageBitmap(userPost.getFile());
            }
            else {
                imageViewPost.setImageResource(R.drawable.post_loading);
            }
        }catch (Exception e)
        {
            imageViewPost.setImageResource(R.drawable.post_loading);
        }

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambar = userPost.getFile();
                Intent report = new Intent(context ,Report.class);
                report.putExtra("EXTRA_SESSION_ID", userPost.getId());
                context.startActivity(report);
            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager session = new SessionManager(context);
                if(session.isLoggedIn()) {
                    new UpPost().execute(userPost.getId());
                    btnUp.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                    btnDown.setImageResource(R.drawable.ic_thumb_down);
                    if (userPost.getLike() == 1)
                    {
                        txtPostPoint.setText(userPost.getLike_count() + " Points");
                    }
                    else if(userPost.getLike() == -1)
                    {
                        txtPostPoint.setText(userPost.getLike_count()+2 + " Points");
                    }
                    else
                    {
                        txtPostPoint.setText(userPost.getLike_count() + 1 + " Points");
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
                    new DownPost().execute(userPost.getId());
                    btnDown.setImageResource(R.drawable.ic_thumb_down_blue_24dp);
                    btnUp.setImageResource(R.drawable.ic_thumb_up);
                    if (userPost.getLike() == -1)
                    {
                        txtPostPoint.setText(userPost.getLike_count() + " Points");
                    }
                    else if(userPost.getLike() == 1)
                    {
                        txtPostPoint.setText(userPost.getLike_count()-2 + " Points");
                    }
                    else
                    {
                        txtPostPoint.setText(userPost.getLike_count() - 1 + " Points");
                    }
                }
                else
                {
                    Toast.makeText(context, "Harap Login Terlebih Dahulu", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, KomentarActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", userPost.getId());
                context.startActivity(intent);
            }
        });

        //scaleImage(imageViewPost);

        if(reachedEndOfList(position)) loadMoreData();

        return convertView;
    }

    private boolean reachedEndOfList(int position) {
        // can check if close or exactly at the end
        return position == userPosts.size() - 1;
    }

    private void loadMoreData() {
       // (UserPostAdapter) context. // Perhaps set flag to indicate you're loading and check flag before proceeding with AsyncTask or whatever
    }

    private class UpPost extends AsyncTask<String, Void, String>
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
                String url = "http://" + HttpHandler.IP + "/guyon/api/post/upvote?access_token=" + HttpHandler.ACCESS_TOKEN;
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
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownPost extends AsyncTask<String, Void, String>
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
                String url = "http://" + HttpHandler.IP + "/guyon/api/post/downvote?access_token=" + HttpHandler.ACCESS_TOKEN;
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
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }
    }
}
