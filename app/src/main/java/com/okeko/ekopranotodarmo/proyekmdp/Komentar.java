package com.okeko.ekopranotodarmo.proyekmdp;

/**
 * Created by Eko Pranotodarmo on 6/13/2017.
 */

public class Komentar
{
    String id;
    String idPost;
    String idReply;
    String username;
    String komen;
    int poin;
    int like;

    public Komentar(String id, String idPost, String idReply, String username, String komen, int poin, int like) {
        this.id = id;
        this.idPost = idPost;
        this.idReply = idReply;
        this.username = username;
        this.komen = komen;
        this.like = like;
        this.poin = poin;
    }

    public String getId() {
        return id;
    }

    public String getIdPost() {
        return idPost;
    }

    public String getIdReply() {
        return idReply;
    }

    public String getUsername() {
        return username;
    }

    public String getKomen() {
        return komen;
    }

    public int getLike() {
        return like;
    }

    public int getPoin() {
        return poin;
    }
}
