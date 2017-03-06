package com.epicodus.bitxbit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.epicodus.bitxbit.ui.LoginActivity;
import com.epicodus.bitxbit.ui.MenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alaina Traxler on 3/6/2017.
 */

public class AuthListenerActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public Context mContext;
    private String mActivityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mContext = this;
        mActivityName = this.getClass().getSimpleName();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Activity Name:", mActivityName);
                    if(mActivityName.equals("LoginActivity")){
                        startActivity(new Intent(mContext, MenuActivity.class));
                    }
                } else {
                    // User is signed out
                    if(!mActivityName.equals("LoginActivity")){
                        startActivity(new Intent(mContext, LoginActivity.class));
                    }
                }
                // ...
            }
        };

//        if(mAuth.getCurrentUser() != null){
//            Log.d("User name:", mAuth.getCurrentUser().getEmail());
//            mAuth.notify();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
