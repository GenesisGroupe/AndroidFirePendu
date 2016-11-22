package com.genesisgroupe.firependu.Fragment;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.genesisgroupe.firependu.R;
import com.genesisgroupe.firependu.model.Game;
import com.genesisgroupe.firependu.model.Turn;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by alex on 21/11/2016.
 */
public class GameFragment extends Fragment {


    private static String GAME_ID_EXTRA = "game.id.extra";

    private DatabaseReference mDatabase;

    private Game game;

    private TextView titleTV;

    private TextView tourTV;

    private TextView player1TV;
    private TextView player2TV;

    private LinearLayout wordLT;

    private LinearLayout lettersLT;

    public static GameFragment newInstance(Game game) {
        GameFragment newFragment = new GameFragment();

        Bundle args = new Bundle();

        args.putString(GAME_ID_EXTRA, game.getName());

        newFragment.setArguments(args);


        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        String gameName = getArguments().getString(GAME_ID_EXTRA);
        mDatabase.child("Games").child(gameName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                reset();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_game, container, false);
        titleTV = (TextView) mainView.findViewById(R.id.titleTV);
        tourTV = (TextView) mainView.findViewById(R.id.tourTV);
        wordLT = (LinearLayout) mainView.findViewById(R.id.wordLayout);
        lettersLT = (LinearLayout) mainView.findViewById(R.id.letterLayout);


        player1TV = (TextView) mainView.findViewById(R.id.player1);
        player2TV = (TextView) mainView.findViewById(R.id.player2);
        reset();

        return mainView;
    }

    private void reset() {
        if (game != null) {
            titleTV.setText(game.getName());
            player1TV.setText("Joueur 1 :"+game.getScorePlayer1());
            player2TV.setText("Joueur 2 :"+game.getScorePlayer2());
            if(game.isFinished()){
                tourTV.setText("Partie terminée");
                tourTV.setTextColor(Color.BLACK);
            }else{
                if (game.isMyTurn()) {
                    tourTV.setText("C'est votre tour !");
                    tourTV.setTextColor(Color.GREEN);
                } else {
                    tourTV.setText("Attendez votre tour");
                    tourTV.setTextColor(Color.RED);
                }

            }

            tourTV.setVisibility(View.VISIBLE);

            wordLT.removeAllViews();
            for (int letterPos = 0; letterPos < game.getWord().length(); letterPos++) {
                String letter = String.valueOf(game.getWord().charAt(letterPos));

                TextView wordTv = new TextView(getActivity());
                if (game.getPlayedLetters().contains((letter))) {
                    wordTv.setText(letter);

                } else {

                    wordTv.setText("_");
                }

                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                llp.setMargins(20, 20, 20, 20); // llp.setMargins(left, top, right, bottom);
                wordTv.setLayoutParams(llp);
                wordTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);


                wordLT.addView(wordTv);

            }

            initLetter();
        } else {
            titleTV.setText("");
            tourTV.setVisibility(View.INVISIBLE);
        }
    }

    private void initLetter(){
        lettersLT.removeAllViews();
        int count =0;
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        LinearLayout horizontalLayout = new LinearLayout(getContext());
        for (char ch: alphabet.toCharArray()) {
            if(((count%5)==0) && (count!=0)){
                lettersLT.addView(horizontalLayout);
                horizontalLayout = new LinearLayout(getContext());
            }

            Button letterTV = new Button(getContext());
            letterTV.setText(String.valueOf(ch));

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            llp.setMargins(10, 10, 10, 10); // llp.setMargins(left, top, right, bottom);
            letterTV.setPadding(10,5,10,5);
            letterTV.setLayoutParams(llp);
            letterTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            letterTV.setWidth(50);

            ShapeDrawable sd = new ShapeDrawable();
            // Specify the shape of ShapeDrawable
            sd.setShape(new RectShape());

            // Specify the border color of shape
            sd.getPaint().setColor(Color.GRAY);

            // Set the border width
            sd.getPaint().setStrokeWidth(10f);

            // Specify the style is a Stroke
            sd.getPaint().setStyle(Paint.Style.STROKE);

            // Finally, add the drawable background to TextView
            letterTV.setBackground(sd);

            if( (game.getPlayedLetters().contains(String.valueOf(ch))) ||
                    (game.isFinished())){
                letterTV.setEnabled(false);
            }else{
                letterTV.setClickable(true);

                letterTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView here = (TextView) v;
                        String letter = here.getText().toString();

                        Turn newTurn = new Turn();
                        newTurn.setLetter(letter);

                        game.addTurn(newTurn);
                        mDatabase.child("Games").child(game.getName()).setValue(game);
                    }
                });
            }


            horizontalLayout.addView(letterTV);
            count++;
        }

        lettersLT.addView(horizontalLayout);


    }
}
