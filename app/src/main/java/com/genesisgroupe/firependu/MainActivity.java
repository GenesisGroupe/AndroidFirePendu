package com.genesisgroupe.firependu;

import android.app.FragmentManager;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;

import com.genesisgroupe.firependu.Fragment.MainFragment;

public class MainActivity extends FragmentActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment mainFragment = new MainFragment();


    }

}
