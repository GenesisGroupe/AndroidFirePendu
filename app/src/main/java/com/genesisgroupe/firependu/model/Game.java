package com.genesisgroupe.firependu.model;

import com.google.firebase.auth.FirebaseAuth;
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

    private User host;

    private User guest;

    private String id;

    private String word;

    private List<Turn> turns;

    public Game() {
        this.host = null;
        this.guest = null;
        this.turns = new ArrayList<Turn>();

    }

    public String getName() {
        //return "alphabet";
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if(getTurns().isEmpty()){
            return (getHost().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        }
        Turn lastTurn = getTurns().get(getTurns().size() - 1);
        return (!lastTurn.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()));
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
        int score =0;
        for(Turn turn:getTurns()){
            if(getWord().contains(String.valueOf(turn.getLetter()))){
                if (turn.getUser().equals(getHost().getUid())){
                    score++;
                    for(Character a:getWord().toCharArray()){
                        if(!getPlayedLetters().contains(String.valueOf(a))){
                            return score;
                        }
                    }
                    score += 2;
                }
            }
        }

        return score;
    }

    @Exclude
    public Integer getScorePlayer2(){
        int score =0;
        for(Turn turn:getTurns()){
            if(getWord().contains(String.valueOf(turn.getLetter()))){
                if (turn.getUser().equals(getGuest().getUid())){
                    score++;
                    for(Character a:getWord().toCharArray()){
                        if(!getPlayedLetters().contains(String.valueOf(a))){
                            return score;
                        }
                    }
                    score += 2;
                }
            }
        }

        return score;
    }

    @Exclude
    public boolean isFinished(){
        for(char letter:getWord().toCharArray()){
            if(!getPlayedLetters().contains(String.valueOf(letter))){
                return false;
            }
        }
        return true;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public void setWord(String word) {
        this.word = word;
    }


    public String getWord(){
        return word;
    }
}
