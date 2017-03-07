package com.epicodus.bitxbit.ui;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epicodus.bitxbit.AuthListenerActivity;
import com.epicodus.bitxbit.Constants;
import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.adapters.FirebaseFromExerciseViewHolder;
import com.epicodus.bitxbit.models.Exercise;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AuthListenerActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    @BindView(R.id.FAB_logout) FloatingActionButton mFAB_Logout;

    @BindView(R.id.FAB_Seed) FloatingActionButton mFAB_Seed;
    @BindView(R.id.RecyclerView_From) RecyclerView mRecyclerView_From;
    @BindView(R.id.spinner) Spinner mSpinner;

    private FirebaseRecyclerAdapter mFromExerciseFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSpinner.setOnItemSelectedListener(this);
        setFromItemTouchListener();

        mFAB_Logout.setOnClickListener(this);
        mFAB_Seed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mFAB_Logout){
            mAuth.signOut();
        }else if(view == mFAB_Seed){
            seedExercisesFromTextFile();
        }
    }

    private void setUpFromExerciseAdapter(){
        DatabaseReference dbExerciseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_EXERCISES);

        DatabaseReference dbKeyRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_SEARCH).child(Constants.DB_EXERCISES);
        DatabaseReference dbDataRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_EXERCISES);

//        mFromExerciseFirebaseAdapter = new FirebaseRecyclerAdapter<Exercise, FirebaseFromExerciseViewHolder>
//                (Exercise.class, R.layout.list_item_from_exercise, FirebaseFromExerciseViewHolder.class, dbExerciseRef) {
//
//            @Override
//            protected void populateViewHolder(FirebaseFromExerciseViewHolder viewHolder, Exercise exercise, int position) {
//                viewHolder.bindExercise(exercise);
//            }
//        };

        mFromExerciseFirebaseAdapter = new FirebaseIndexRecyclerAdapter<Exercise, FirebaseFromExerciseViewHolder>
                (Exercise.class, R.layout.list_item_from_exercise, FirebaseFromExerciseViewHolder.class, dbKeyRef, dbDataRef) {
                     public void populateViewHolder(FirebaseFromExerciseViewHolder viewHolder, Exercise exercise, int position) {
                             viewHolder.bindExercise(exercise);
                         }
                 };

        mRecyclerView_From.setHasFixedSize(false);
        mRecyclerView_From.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_From.setAdapter(mFromExerciseFirebaseAdapter);
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


    private void setFromItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                mFromExerciseFirebaseAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView_From);
    }


    // Reads the exercises from a txt file and then builds their models, places them in the database and indexes them for search
    public void seedExercisesFromTextFile(){
        Toast.makeText(MainActivity.this, "Seed active", Toast.LENGTH_SHORT).show();

        DatabaseReference dbExercises = FirebaseDatabase.getInstance().getReference().child(Constants.DB_EXERCISES);
        DatabaseReference dbSearchExercises = FirebaseDatabase.getInstance().getReference().child(Constants.DB_SEARCH).child(Constants.DB_EXERCISES);

        dbExercises.removeValue();
        dbSearchExercises.removeValue();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("seedlists/exercises.txt")));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                String name = mLine.substring(0,mLine.indexOf("["));
                String type = mLine.substring(mLine.indexOf("[") + 1,mLine.indexOf("]"));

                Exercise exercise = new Exercise(name, type);

                if(mLine.contains("<")){
                    String altNames = mLine.substring(mLine.indexOf("<") + 1,mLine.indexOf(">"));
                    exercise.addAltName(altNames);
                }

                DatabaseReference pushRef = dbExercises.push();
                String pushId = pushRef.getKey();
                exercise.setPushId(pushId);
                pushRef.setValue(exercise);

                dbSearchExercises.child(pushId).setValue(exercise.getName().toLowerCase());

            }

        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}
