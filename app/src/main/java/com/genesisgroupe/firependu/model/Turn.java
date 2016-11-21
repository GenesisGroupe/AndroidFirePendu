package com.genesisgroupe.firependu.model;

/**
 * Created by alex on 21/11/2016.
 */
public class Turn {

    private String letter;

    private User user;

    public Turn(){

    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
