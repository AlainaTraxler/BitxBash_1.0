package com.epicodus.bitxbit.ui;

import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.epicodus.bitxbit.AuthListenerActivity;
import com.epicodus.bitxbit.Constants;
import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.adapters.FirebaseFromExerciseViewHolder;
import com.epicodus.bitxbit.models.Exercise;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AuthListenerActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    @BindView(R.id.FAB_logout) FloatingActionButton mFAB_Logout;
    @BindView(R.id.RecyclerView_From) RecyclerView mRecyclerView_From;
    @BindView(R.id.spinner) Spinner mSpinner;

    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        mSpinner.setOnItemSelectedListener(this);

        mFAB_Logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mFAB_Logout){
            mAuth.signOut();
        }
    }


    private void setUpFromExerciseAdapter(){
        DatabaseReference dbExerciseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_EXERCISES);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Exercise, FirebaseFromExerciseViewHolder>
                (Exercise.class, R.layout.list_item_from_exercise, FirebaseFromExerciseViewHolder.class, dbExerciseRef) {

            @Override
            protected void populateViewHolder(FirebaseFromExerciseViewHolder viewHolder, Exercise exercise, int position) {
                viewHolder.bindExercise(exercise);
            }
        };
        mRecyclerView_From.setHasFixedSize(false);
        mRecyclerView_From.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_From.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == 0){
            setUpFromExerciseAdapter();
        }else{
            mRecyclerView_From.setAdapter(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
