package com.okeko.ekopranotodarmo.proyekmdp;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Eko Pranotodarmo on 5/12/2017.
 */

public class UserPost
{
    private String id;
    private int idkategori;
    private String caption;
    private Bitmap file;
    private String username;
    private int like_count;
    private int comment_count;
    private boolean status;
    private String created;
    private String updated;
    private int like;

    public UserPost(String id, int idkategori, String caption, Bitmap file, String username, int like_count, int comment_count, boolean status, String created, String updated, int like) {
        this.id = id;
        this.idkategori = idkategori;
        this.caption = caption;
        this.file = file;
        this.username = username;
        this.like_count = like_count;
        this.comment_count = comment_count;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.like = like;
    }

    public String getId() {
        return id;
    }

    public int getIdkategori() {
        return idkategori;
    }

    public String getCaption() {
        return caption;
    }

    public Bitmap getFile() {
        return file;
    }

    public String getUsername() {
        return username;
    }

    public int getLike_count() {
        return like_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public boolean isStatus() {
        return status;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public int getLike() {
        return like;
    }
}
