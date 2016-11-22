package com.genesisgroupe.firependu.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alex on 21/11/2016.
 */
@IgnoreExtraProperties
public class Game {

    private String name;

    private List<User> users;

    private List<Turn> turns;

    public Game() {
        this.users = new ArrayList<User>();
        this.turns = new ArrayList<Turn>();

        Turn turn = new Turn();
        turn.setLetter("a");
        turns.add(turn);

        Turn turn2 = new Turn();
        turn2.setLetter("d");
        turns.add(turn2);


        Turn turn3 = new Turn();
        turn3.setLetter("l");
        turns.add(turn3);
    }

    public String getName() {
        //return "alphabet";
        return name;
    }

    public String getWord(){
        return "alphabet";
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

    public void addTurn(Turn turn){
        turns.add(turn);
    }

    @Exclude
    public boolean isMyTurn() {
        Random random = new Random();
        return random.nextBoolean();
    }

    @Exclude
    public List<String> getPlayedLetters() {
        ArrayList<String> letters = new ArrayList<String>();
        for (Turn turn : turns
                ) {
            letters.add((turn.getLetter()));
        }


        return letters;
    }

    @Exclude
    public Integer getScorePlayer1(){
        return 5;
    }

    @Exclude
    public Integer getScorePlayer2(){
        return 6;
    }

    @Exclude
    public boolean isFinished(){
        return false;
    }
}
