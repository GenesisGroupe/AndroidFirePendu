package com.genesisgroupe.firependu.Fragment;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.genesisgroupe.firependu.GameSelectedI;
import com.genesisgroupe.firependu.R;
import com.genesisgroupe.firependu.WordGenerator;
import com.genesisgroupe.firependu.model.Game;
import com.genesisgroupe.firependu.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 765;

    SignInButton signInButton;
    Button disconnectButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button addButton;


    private ListView gameListView;

    private FirebaseListAdapter<Game> mAdapter;

    private GameSelectedI gameSelectedInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_list, container, false);

        String token = getString(R.string.default_web_client_id_2);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();


        if (mGoogleApiClient == null) {
            try {

                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .enableAutoManage(getActivity(), this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
            } catch (Exception e) {

            }
        }


        signInButton = (SignInButton) mainView.findViewById(R.id.sign_in_button);
        disconnectButton = (Button) mainView.findViewById(R.id.disconnect_button);
        gameListView = (ListView) mainView.findViewById(R.id.gameListView);
        addButton = (Button) mainView.findViewById(R.id.addButton);
        addButton.setText("Créer une partie");

        signInButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mAdapter = new FirebaseListAdapter<Game>(getActivity(), Game.class, android.R.layout.two_line_list_item, mDatabase.child("Games")) {

                        @Override
                        protected void populateView(View view, Game game, int position) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            ((TextView) view.findViewById(android.R.id.text1)).setText(game.getName());
                            TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
                            if ((game.getGuest() == null) ||
                                    (game.getGuest().getUid().equals(currentUser.getUid())) ||
                                    (game.getHost().getUid().equals(currentUser.getUid()))
                                    ) {
                                if (game.isMyTurn()) {
                                    tv2.setText("votre tour");
                                    tv2.setBackgroundColor(Color.GREEN);
                                } else {
                                    tv2.setText("A l'adversaire");
                                    tv2.setBackgroundColor(Color.BLUE);
                                }

                            } else {
                                tv2.setText("INDISPONIBLE");
                                tv2.setBackgroundColor(Color.RED);
                            }
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(0, 0, 0, 50); // llp.setMargins(left, top, right, bottom);
                            tv2.setLayoutParams(llp);


                        }
                    };
                    gameListView.setAdapter(mAdapter);
                } else {
                    if (mAdapter != null) {
                        mAdapter.cleanup();
                    }
                    gameListView.setAdapter(null);
                }


            }
        });

        addButton.setOnClickListener(this);

        gameListView.setOnItemLongClickListener(this);
        gameListView.setOnItemClickListener(this);

        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        gameSelectedInterface = (GameSelectedI) context;

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "listener" + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disconnect_button:
                Toast.makeText(getActivity(), "disconnect", Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.addButton:
                addGame();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Game game = mAdapter.getItem(position);
        mDatabase.child("Games").child(game.getName()).removeValue();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(getActivity(), "Google Auth failed." + result.getStatus(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Game game = mAdapter.getItem(position);
        gameSelectedInterface.onGameSelected(game);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential);
    }

    public void addGame() {
        User me = new User();
        me.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        me.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Game game = new Game();
        game.setHost(me);

        WordGenerator wg = new WordGenerator();
        game.setWord(wg.getWord());

        game.setName("Un partie créée par Android");
        String key = FirebaseDatabase.getInstance().getReference().child("Games").push().getKey();
        game.setId(key);
        FirebaseDatabase.getInstance().getReference().child("Games").child(key).setValue(game);

    }
}
