package com.epicodus.bitxbit.ui;

import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.epicodus.bitxbit.AuthListenerActivity;
import com.epicodus.bitxbit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends AuthListenerActivity implements View.OnClickListener{
    @BindView(R.id.FAB_logout) FloatingActionButton mFAB_Logout;
//    @BindView(R.id.textView_Workout) TextView mTextView_Workout;
//    @BindView(R.id.textView_Routine) TextView mTextView_Routine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        mFAB_Logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mFAB_Logout){
            mAuth.signOut();
        }
    }
}