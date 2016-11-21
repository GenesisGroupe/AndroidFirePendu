package com.genesisgroupe.firependu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 21/11/2016.
 */
public class Game {

    private String name;

    private List<User> users;

    private List<Turn> turns;

    public Game(){
        this.users = new ArrayList<User>();
        this.turns = new ArrayList<Turn>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Turn> getTurns() {
        return turns;
    }

    public void setTurns(List<Turn> turns) {
        this.turns = turns;
    }
}
