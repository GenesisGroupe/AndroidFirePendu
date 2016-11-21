package com.genesisgroupe.firependu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.genesisgroupe.firependu.model.Game;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,AdapterView.OnItemLongClickListener{

    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 765;

    SignInButton signInButton;
    Button disconnectButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private ListView gameListView;

   private FirebaseListAdapter<Game> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = getString(R.string.default_web_client_id_2);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        disconnectButton = (Button) findViewById(R.id.disconnect_button);
        gameListView = (ListView) findViewById(R.id.gameListView);

        signInButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAdapter = new FirebaseListAdapter<Game>(this, Game.class, android.R.layout.two_line_list_item, mDatabase.child("Games")) {
            @Override
            protected void populateView(View view, Game game, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(game.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(game.getName());

            }
        };
        gameListView.setAdapter(mAdapter);
        gameListView.setOnItemLongClickListener(this);
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
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
                Toast.makeText(MainActivity.this, "Google Auth failed." + result.getStatus(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "listener" + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firependu", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("firependu", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("firependu", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disconnect_button:
                Toast.makeText(this,"disconnect",Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Game game = mAdapter.getItem(position);
        mDatabase.child("Games").child(game.getName()).removeValue();
        return true;
    }
}
