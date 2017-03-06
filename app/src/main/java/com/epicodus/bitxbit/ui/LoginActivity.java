package com.epicodus.bitxbit.ui;

import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.bitxbit.AuthListenerActivity;
import com.epicodus.bitxbit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AuthListenerActivity implements View.OnClickListener{
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
        String email = mEditText_Email.getText().toString();
        String password = mEditText_password.getText().toString();

        if(view == mButton_SignIn){
            if(isEmailValid(email) && isPasswordValid(password)){
                signIn(email, password);
            }
        }else {

            if(isEmailValid(email) && isPasswordValid(password)){
                createAccount(email, password);
            }
        }
    }

    public void signIn(String _email, String _password){
        mAuth.signInWithEmailAndPassword(_email, _password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void createAccount(String _email, String _password){
        mAuth.createUserWithEmailAndPassword(_email, _password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public boolean isEmailValid(String email) {
        if(email != null){
            boolean isValid = false;

            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            CharSequence inputStr = email;

            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);

            if (matcher.matches()) {
                isValid = true;
            }else{
                mEditText_Email.setError("Invalid email");
            }

            return isValid;
        }else{
            mEditText_Email.setError("Please enter your email");
            return false;
        }
    }

    public boolean isPasswordValid(String password){
        if (password != null) {
            if (password.length() < 6) {
                mEditText_password.setError("Password must be at least six characters");
                return false;
            } else {
                return true;
            }
        } else {
            mEditText_password.setError("Please enter your password");
            return false;
        }
    }
}
