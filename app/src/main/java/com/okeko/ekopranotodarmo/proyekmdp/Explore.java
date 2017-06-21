package com.okeko.ekopranotodarmo.proyekmdp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by Eko Pranotodarmo on 5/13/2017.
 */

public class Explore
{
    String id;
    String category;
    ArrayList<Bitmap> images = new ArrayList<>();

    public Explore(String id, String category, ArrayList<Bitmap> image) {
        this.id = id;
        this.category = category;
        this.images = image;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public String getId() {
        return id;
    }
}
