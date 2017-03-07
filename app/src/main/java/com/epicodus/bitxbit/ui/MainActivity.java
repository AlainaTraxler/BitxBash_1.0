package com.epicodus.bitxbit.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epicodus.bitxbit.AuthListenerActivity;
import com.epicodus.bitxbit.Constants;
import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.adapters.FirebaseFromExerciseViewHolder;
import com.epicodus.bitxbit.adapters.FromExerciseAdapter;
import com.epicodus.bitxbit.models.Exercise;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AuthListenerActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    @BindView(R.id.FAB_logout) FloatingActionButton mFAB_Logout;
    @BindView(R.id.FAB_Seed) FloatingActionButton mFAB_Seed;
    @BindView(R.id.FAB_Refresh) FloatingActionButton mFAB_Refresh;
    @BindView(R.id.searchView) SearchView mSearchView;
    @BindView(R.id.RecyclerView_From) RecyclerView mRecyclerView_From;
    @BindView(R.id.spinner) Spinner mSpinner;

    private FirebaseRecyclerAdapter mFromExerciseFirebaseAdapter;
    private ArrayList<Exercise> mFromExerciseList = new ArrayList<Exercise>();
    private ArrayList<Exercise> mFilteredFromExerciseList = new ArrayList<Exercise>();
    private FromExerciseAdapter mFromExerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSpinner.setOnItemSelectedListener(this);
        setFromItemTouchListener();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s == ""){
                    setUpFromExerciseAdapter(null);
                }else{
                    setUpFromExerciseAdapter(s.toLowerCase());
                }
                return false;
            }
        });

        mFAB_Logout.setOnClickListener(this);
        mFAB_Seed.setOnClickListener(this);
        mFAB_Refresh.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == mFAB_Logout){
            mAuth.signOut();
        }else if(view == mFAB_Seed){
            seedExercisesFromTextFile();
        }else if(view == mFAB_Refresh){
            Log.d("Refresh!", "Lovely!");
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    }

    private void fetchExercises(){
        DatabaseReference dbExerciseRef = FirebaseDatabase.getInstance().getReference().child(Constants.DB_EXERCISES);

        dbExerciseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mFromExerciseList.add(snapshot.getValue(Exercise.class));
                }
                setUpFromExerciseAdapter(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Exercise> filterExercises(String query){
        mFilteredFromExerciseList.clear();

            for(Exercise exercise : mFromExerciseList){
                if(query != null) {
                    if (exercise.getName().toLowerCase().contains(query)) {
                        mFilteredFromExerciseList.add(exercise);
                    }
                }else mFilteredFromExerciseList.add(exercise);
            }

        Log.d("Returning list", String.valueOf(mFilteredFromExerciseList.size()));
        return mFilteredFromExerciseList;
    }

    private void setUpFromExerciseAdapter(String query){
        mFromExerciseAdapter = new FromExerciseAdapter(getApplicationContext(), filterExercises(query));
        mRecyclerView_From.setHasFixedSize(true);
        mRecyclerView_From.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_From.setAdapter(mFromExerciseAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == 0){
            if(mFromExerciseList.size() == 0){
                Log.d(">>>>>", "Triggering first time fetch");
                fetchExercises();
            }else{
                setUpFromExerciseAdapter(null);
            }
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
                Log.d("Swiped: ", mFilteredFromExerciseList.get(viewHolder.getAdapterPosition()).getName());
                mFromExerciseAdapter.notifyDataSetChanged();
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
