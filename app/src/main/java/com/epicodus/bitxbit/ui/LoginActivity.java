package com.epicodus.bitxbit.ui;

import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epicodus.bitxbit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.editText_email) EditText mEditText_Email;
    @BindView(R.id.editText_password) EditText mEditText_password;
    @BindView(R.id.button_signIn) Button mButton_SignIn;
    @BindView(R.id.textView_signUp) TextView mTextView_SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mButton_SignIn.setOnClickListener(this);
        mTextView_SignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mButton_SignIn){

        }else{

        }
    }
}
