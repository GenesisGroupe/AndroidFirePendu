package com.genesisgroupe.firependu.model;

/**
 * Created by alex on 21/11/2016.
 */
public class User {
    private String nickName;
    private Integer score;

    public User() {

    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
