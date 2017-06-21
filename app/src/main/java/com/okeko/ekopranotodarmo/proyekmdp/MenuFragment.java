package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;
    private Integer indexActivity;

    private int[] icButton;
    private int[] labelButton;

    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(int param1) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            indexActivity = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInlatedView = inflater.inflate(R.layout.fragment_menu, container, false);
        int[] iconList = {R.drawable.ic_home,R.drawable.ic_search,R.drawable.ic_person};
        int[] icButton = {R.id.icHome,R.id.icSearch,R.id.icProfile};
        int[] labelButton = {R.id.labelHome,R.id.labelSearch,R.id.labelProfile};

        ImageView icon = (ImageView) myInlatedView.findViewById(icButton[indexActivity]);
        Drawable myIcon = getResources().getDrawable( iconList[indexActivity] );
        ColorFilter filter = new LightingColorFilter( ContextCompat.getColor(MenuFragment.this.getContext(), R.color.colorPrimary), ContextCompat.getColor(MenuFragment.this.getContext(), R.color.colorPrimary));
        myIcon.setColorFilter(filter);
        icon.setImageDrawable(myIcon);
        TextView label = (TextView) myInlatedView.findViewById(labelButton[indexActivity]);
        label.setTextColor(ContextCompat.getColor(MenuFragment.this.getContext(), R.color.colorPrimary));

        LinearLayout btnHome = (LinearLayout) myInlatedView.findViewById(R.id.btn_home);
        LinearLayout btnSearch = (LinearLayout) myInlatedView.findViewById(R.id.btn_serach);
        //LinearLayout btnVideo = (LinearLayout) myInlatedView.findViewById(R.id.btn_video);
        //LinearLayout btnNews = (LinearLayout) myInlatedView.findViewById(R.id.btn_news);
        LinearLayout btnProfile = (LinearLayout) myInlatedView.findViewById(R.id.btn_profile);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
                getActivity().finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExploreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
                getActivity().finish();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
                getActivity().finish();
            }
        });

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
}
