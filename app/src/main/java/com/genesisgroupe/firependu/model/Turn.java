package com.genesisgroupe.firependu.model;

/**
 * Created by alex on 21/11/2016.
 */
public class Turn {

    private String letter;

    private String user;

    private String id;


    public Turn(){

    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
