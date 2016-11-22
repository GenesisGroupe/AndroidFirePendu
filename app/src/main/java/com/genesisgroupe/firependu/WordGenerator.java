package com.genesisgroupe.firependu;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex on 22/11/2016.
 */
public class WordGenerator {

    private ArrayList<String> words ;

    public WordGenerator(){
        words = new ArrayList<String>();

        words.add("koala");
        words.add("generateur");
        words.add("jazz");
        words.add("hibiscus");
        words.add("kitchenette");
        words.add("ronflant");
        words.add("piqure");
    }

    public String getWord(){
        Random random = new Random();
        int index = random.nextInt(words.size());
        return words.get(index);

    }
}
