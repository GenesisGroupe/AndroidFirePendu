package com.genesisgroupe.firependu;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.genesisgroupe.firependu.Fragment.GameFragment;
import com.genesisgroupe.firependu.Fragment.MainFragment;
import com.genesisgroupe.firependu.model.Game;

public class MainActivity extends FragmentActivity implements GameSelectedI {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment mainFragment = new MainFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.main_fragment, mainFragment);

// Commit the transaction
        transaction.commit();

    }

    @Override
    public void onGameSelected(Game game) {
        GameFragment mainFragment = GameFragment.newInstance(game);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.addToBackStack("random2");
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.main_fragment, mainFragment);

// Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
