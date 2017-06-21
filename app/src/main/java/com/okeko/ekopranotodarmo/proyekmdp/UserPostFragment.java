package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPostFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView listData;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private UserPostAdapter userPostAdapter;

    private String TAG = UserPostFragment.class.getSimpleName();

    private int preLast;
    private boolean loadData;
    private boolean viewReady;
    private int count;

    public UserPostFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static UserPostFragment newInstance(String param1) {
        UserPostFragment fragment = new UserPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInlatedView = inflater.inflate(R.layout.fragment_user_post, container, false);


        listData = (ListView) myInlatedView.findViewById(R.id.list_user_post);
        userPostAdapter = new UserPostAdapter(UserPostFragment.this.getContext(), userPosts);
        listData.setAdapter(userPostAdapter);
        listData.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch(view.getId())
                {
                    case R.id.list_user_post:

                        // Make your calculation stuff here. You have all your
                        // needed info from the parameters of this function.

                        // Sample calculation to determine if the last
                        // item is fully visible.
                        final int lastItem = firstVisibleItem + visibleItemCount;

                        if(lastItem == totalItemCount)
                        {
                            if(preLast!=lastItem)
                            {
                                //to avoid multiple calls for last item
                                preLast = lastItem;
                                count += 5;
                                new getPost().execute();
                            }
                        }
                }
            }
        });
        if(userPosts.size()==0) {
            if(HttpHandler.AUTHORIZATION_CODE.equals("")){
                new getKey().execute();
            }else{
                new getPost().execute();
            }
            count = 0;
        }
        return myInlatedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class getPost extends AsyncTask<Void, Void, Void> {

        private SQLiteHandler db;
        private SessionManager session;

        private ArrayList<UserPost> tempUserPost = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity().getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String nextParam = "start=" + count + "&end=" + count + 5;
            if (mParam1.contains("?")) {
                mParam1 = mParam1 + "&" + nextParam;
            } else {
                mParam1 = mParam1 + "?" + nextParam;
            }
            mParam1 += "&access_token=" + HttpHandler.ACCESS_TOKEN;
            String url = "http://" + HttpHandler.IP + "/guyon/api/post/" + mParam1;

            session = new SessionManager(UserPostFragment.this.getContext());
            if (session.isLoggedIn()) {
                db = new SQLiteHandler(UserPostFragment.this.getContext());
                HashMap<String, String> user = db.getUserDetails();
                String username = user.get("email");
                url = "http://" + HttpHandler.IP + "/guyon/api/post/" + mParam1 + "&user=" + username;
            }
            url = url.replace(" ", "%20");
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray json = new JSONArray(jsonStr);
                    tempUserPost.clear();
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject e = json.getJSONObject(i);
                        String id = e.getString("id");
                        int idkategori = e.getInt("idkategori");
                        String caption = e.getString("caption");
                        String namaFile = e.getString("namafile");
                        Bitmap file = null;
                        InputStream in = null;
                        URL urlImage = null;
                        try {
                            namaFile = namaFile.replace("localhost", HttpHandler.IP);
                            urlImage = new URL(namaFile);

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            file = BitmapFactory.decodeStream(urlImage.openConnection().getInputStream(), null, options);

                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                            Log.e(TAG, "image error: " + e1.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            Log.e(TAG, "image error: " + e1.getMessage());
                        }

                        String username = e.getString("username");
                        ;
                        int like_count = e.getInt("like_count");
                        int comment_count = e.getInt("comment_count");
                        String tempStatus = e.getString("status");
                        boolean status = false;
                        if (tempStatus.equalsIgnoreCase("1")) status = true;
                        String created = e.getString("created");
                        String updated = e.getString("updated");
                        int like = e.getInt("like");
                        tempUserPost.add(new UserPost(id, idkategori, caption, file, username, like_count, comment_count, status, created, updated, like));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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
            for (int i = 0; i < tempUserPost.size(); i++) {
                userPosts.add(tempUserPost.get(i));
            }
            userPostAdapter.notifyDataSetChanged();
        }
    }

    private class getKey extends AsyncTask<Void, Void, Void> {

        private SQLiteHandler db;
        private SessionManager session;

        private ArrayList<UserPost> tempUserPost = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = "http://" + HttpHandler.IP + "/guyon/api/oauth2/authorize?response_type=code&client_id=" + HttpHandler.CLIENT_ID +"&state=xyz&redirect_uri=" + HttpHandler.REDIRECT_URL;

            session = new SessionManager(UserPostFragment.this.getContext());
            url = url.replace(" ", "%20");
            HashMap<String, String> param = new HashMap<>();
            param.put("authorized", "yes");
            String jsonStr = sh.getAuthorizationCode(url, param);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    String code = json.getString("code");
                    HttpHandler.AUTHORIZATION_CODE = code;
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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
            new getToken().execute();
        }
    }

    private class getToken extends AsyncTask<Void, Void, Void> {

        private SQLiteHandler db;
        private SessionManager session;

        private ArrayList<UserPost> tempUserPost = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = "http://" + HttpHandler.IP + "/guyon/api/oauth2/token";

            session = new SessionManager(UserPostFragment.this.getContext());
            url = url.replace(" ", "%20");
            HashMap<String, String> param = new HashMap<>();
            param.put("grant_type", "authorization_code");
            param.put("code", HttpHandler.AUTHORIZATION_CODE);
            param.put("redirect_uri", HttpHandler.REDIRECT_URL);
            String jsonStr = sh.getToken(url, param);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    String accToken = json.getString("access_token");
                    String refToken = json.getString("refresh_token");
                    HttpHandler.ACCESS_TOKEN = accToken;
                    HttpHandler.REFRESH_TOKEN = refToken;
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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
            new getPost().execute();
        }
    }
}
