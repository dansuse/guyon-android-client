package com.okeko.ekopranotodarmo.proyekmdp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eko Pranotodarmo on 5/13/2017.
 */

public class ExploreAdapter extends ArrayAdapter<Explore> {
    private Context context;
    private ArrayList<Explore> explores;

    public ExploreAdapter(Context context, ArrayList<Explore> explores)
    {
        super(context, R.layout.row_item_explore, explores);
        this.context = context;
        this.explores = explores;
    }

    @Override
    public Explore getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Explore explore = getItem(position);
        if(convertView == null) {convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_explore,parent,false);}

        ArrayList<Integer> listIdImageView = new ArrayList<>();
        listIdImageView.add(R.id.explore_image_0);
        listIdImageView.add(R.id.explore_image_1);
        listIdImageView.add(R.id.explore_image_2);
        listIdImageView.add(R.id.explore_image_3);

        ArrayList<SquareImageView> imageViews = new ArrayList<>();
        for(int i = 0 ; i < 4 ; i++)
        {
            imageViews.add((SquareImageView) convertView.findViewById(listIdImageView.get(i)));
            //Bitmap bitmapImage = BitmapFactory.decodeResource(((AppCompatActivity) context).getResources(), R.drawable.try_image_7);
            //Drawable d = new BitmapDrawable(((AppCompatActivity) context).getResources(), bitmapImage);
            try
            {
                if(explore.getImages().get(i) != null)
                {
                    imageViews.get(i).setImageBitmap(explore.getImages().get(i));
                }
                else
                {
                    imageViews.get(i).setImageResource(R.drawable.post_loading);
                }
            }catch (Exception e)
            {
                imageViews.get(i).setImageResource(R.drawable.post_loading);
            }
        }

        TextView txtCategory = (TextView) convertView.findViewById(R.id.txt_explore_category);
        txtCategory.setText(explore.getCategory());

        return convertView;
    }
}

