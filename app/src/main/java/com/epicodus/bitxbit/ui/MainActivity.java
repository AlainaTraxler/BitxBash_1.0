package com.epicodus.bitxbit.ui;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.epicodus.bitxbit.AuthListenerActivity;
import com.epicodus.bitxbit.Constants;
import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.adapters.FromExerciseAdapter;
import com.epicodus.bitxbit.adapters.ToExerciseAdapter;
import com.epicodus.bitxbit.models.Exercise;
import com.epicodus.bitxbit.models.Workout;
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
    @BindView(R.id.FAB_Done) FloatingActionButton mFAB_Done;
    @BindView(R.id.FAB_Clear) FloatingActionButton mFAB_Clear;
    @BindView(R.id.FAB_Save) FloatingActionButton mFAB_Save;
    @BindView(R.id.searchView) SearchView mSearchView;
    @BindView(R.id.RecyclerView_From) RecyclerView mRecyclerView_From;
    @BindView(R.id.RecyclerView_To) RecyclerView mRecyclerView_To;
    @BindView(R.id.spinner) Spinner mSpinner;

    private ArrayList<Exercise> mFromExerciseList = new ArrayList<Exercise>();
    private ArrayList<Exercise> mToExerciseList = new ArrayList<Exercise>();
    private ArrayList<Exercise> mFilteredFromExerciseList = new ArrayList<Exercise>();
    private FromExerciseAdapter mFromExerciseAdapter;
    private ToExerciseAdapter mToExerciseAdapter;

    DatabaseReference dbRef;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        userId = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        setUpToExerciseAdapter();
        mSpinner.setOnItemSelectedListener(this);
        setFromItemTouchListener();
        setToItemTouchListener();

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
        mFAB_Clear.setOnClickListener(this);
        mFAB_Done.setOnClickListener(this);
        mFAB_Save.setOnClickListener(this);
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
        }else if(view == mFAB_Done) {
            if (validateSelected(mToExerciseList) && validateFields(mToExerciseList)) {
                Toast.makeText(MainActivity.this, "Workout completed", Toast.LENGTH_SHORT).show();

                Workout workout = new Workout(mToExerciseList, null);
                DatabaseReference pushRef = dbRef.child(Constants.DB_USERS).child(userId).child(Constants.DB_WORKOUTS).push();
                workout.setPushId(pushRef.getKey());
                pushRef.setValue(workout);
            }
        }else if(view == mFAB_Clear){
            mToExerciseList.clear();
            mToExerciseAdapter.notifyDataSetChanged();
        }else if(view == mFAB_Save){
            if(validateSelected(mToExerciseList) && validateFieldsAllowEmpty(mToExerciseList)){
                saveRoutine();
            }
        }
    }

    private void saveRoutine(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name routine");

        final EditText input = new EditText(MainActivity.this);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setMaxLines(1);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = input.getText().toString().trim();
                        if(name.length() == 0){
                            Toast.makeText(mContext, "Please name this routine", Toast.LENGTH_SHORT).show();
                        }else{
                            Workout workout = new Workout(mToExerciseList, name);
                            
                            DatabaseReference pushRef = dbRef.child(Constants.DB_USERS).child(userId).child(Constants.DB_ROUTINES).push();
                            workout.setPushId(pushRef.getKey());
                            pushRef.setValue(workout);

                            Toast.makeText(mContext, "Routine saved", Toast.LENGTH_SHORT).show();
                            
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void fetchExercises(){
        DatabaseReference dbExerciseRef = dbRef.child(Constants.DB_EXERCISES);

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

    private void setUpToExerciseAdapter(){
        mToExerciseAdapter = new ToExerciseAdapter(getApplicationContext(), mToExerciseList);
        mRecyclerView_To.setHasFixedSize(true);
        mRecyclerView_To.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_To.setAdapter(mToExerciseAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == 0){
            if(mFromExerciseList.size() == 0){
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

                Exercise exercise = new Exercise();
                exercise = exercise.clone(mFilteredFromExerciseList.get(viewHolder.getAdapterPosition()));

                mToExerciseList.add(exercise);
                mToExerciseAdapter.notifyItemInserted(mFromExerciseList.size());
                Log.d("ToExercise size: ", String.valueOf(mToExerciseList.size()));

                mFromExerciseAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView_From);
    }

    private void setToItemTouchListener(){
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                int fromPosition = source.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                mToExerciseAdapter.notifyItemMoved(fromPosition, toPosition);

                Exercise exercise = mToExerciseList.get(fromPosition);

                if(fromPosition > toPosition){
                    mToExerciseList.add(toPosition, exercise);
                    mToExerciseList.remove(fromPosition + 1);
                }else{
                    mToExerciseList.add(toPosition + 1, exercise);
                    mToExerciseList.remove(fromPosition);
                }

                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }


            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                mToExerciseList.remove(viewHolder.getAdapterPosition());
                mToExerciseAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView_To);
    }


    // Reads the exercises from a txt file and then builds their models, places them in the database and indexes them for search
    public void seedExercisesFromTextFile(){
        Toast.makeText(MainActivity.this, "Seed active", Toast.LENGTH_SHORT).show();

        DatabaseReference dbExercises = dbRef.child(Constants.DB_EXERCISES);
        DatabaseReference dbSearchExercises = dbRef.child(Constants.DB_SEARCH).child(Constants.DB_EXERCISES);

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

    //----- Validation functions -----//
    public Boolean validateName(String name) {
        if (name.equals("")) {
            Toast.makeText(mContext, "Please name this routine", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean validateSelected(ArrayList<Exercise> exercises){
        if(exercises.size() == 0){
            Toast.makeText(mContext, "No exercises selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean validateFields(ArrayList<Exercise> exercises){
        for(int i = 0; i < exercises.size(); i++){
            Exercise exercise = exercises.get(i);
            if(exercise.getType().equals(Constants.TYPE_WEIGHT)){
                if(exercise.getSets() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of sets for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(exercise.getReps() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of reps for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(exercise.getWeight() <= 0){
                    Toast.makeText(mContext, "Please enter a valid weight for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_AEROBIC)){
                String time = exercise.getTime();
                if(time.length() == 0 || !time.contains(":")){
                    Toast.makeText(MainActivity.this, "Please enter a valid time for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    String minutes = time.substring(0,time.indexOf(":"));
                    String seconds = time.substring(time.indexOf(":") + 1, time.length());

                    if(minutes.length() <=0 || seconds.length() != 2 || Integer.parseInt(minutes) + Integer.parseInt(seconds) <= 0){
                        Toast.makeText(MainActivity.this, "Please enter a valid time for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if(exercise.getDistance() <= 0){
                    Toast.makeText(mContext, "Please enter a valid distance for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_BODYWEIGHT)){
                if(exercise.getSets() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of sets for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(exercise.getReps() <= 0){
                    Toast.makeText(mContext, "Please enter a valid number of reps for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else if(exercise.getType().equals(Constants.TYPE_TIME)){
                String time = exercise.getTime();
                if(time.length() == 0 || !time.contains(":")){
                    Toast.makeText(MainActivity.this, "Please enter a valid time for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    String minutes = time.substring(0,time.indexOf(":"));
                    String seconds = time.substring(time.indexOf(":") + 1, time.length());

                    if(minutes.length() <=0 || seconds.length() != 2 || Integer.parseInt(minutes) + Integer.parseInt(seconds) <= 0){
                        Toast.makeText(MainActivity.this, "Please enter a valid time for " + exercise.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Boolean validateFieldsAllowEmpty(ArrayList<Exercise> exercises){
        for(int i = 0; i < exercises.size(); i++){
            Exercise exercise = exercises.get(i);
            if(exercise.getType().equals(Constants.TYPE_AEROBIC) || exercise.getType().equals(Constants.TYPE_TIME)){
                String time = exercise.getTime();

                if(time.length() == 0){
                    return true;
                }else if(!time.contains(":")){
                    Toast.makeText(MainActivity.this, "Please enter a valid time for " + exercise.getName() + ", or leave blank", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    String minutes = time.substring(0,time.indexOf(":"));
                    String seconds = time.substring(time.indexOf(":") + 1, time.length());

                    if(minutes.length() <=0 || seconds.length() != 2){
                        Toast.makeText(MainActivity.this, "Please enter a valid time for " + exercise.getName() + ", or leave blank", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
