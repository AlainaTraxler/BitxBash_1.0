package com.epicodus.bitxbit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.bitxbit.Constants;
import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.models.Exercise;
import com.epicodus.bitxbit.utilities.DoubleFilterMinMax;
import com.epicodus.bitxbit.utilities.InputFilterMinMax;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 3/7/2017.
 */

public class ToExerciseAdapter extends RecyclerView.Adapter<ToExerciseAdapter.ExerciseViewHolder> {
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<Exercise> mExerciseReference = new ArrayList<>();
    private Context mContext;
    private ToExerciseAdapter mAdapter;

    private ExerciseViewHolder mViewHolder;
    private RecyclerView mRecyclerView;

    public ToExerciseAdapter(Context context, ArrayList<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
        mAdapter = this;
    }

    public void setReference(ArrayList<Exercise> exercises){
        mExerciseReference = exercises;
    }

    @Override
    public ToExerciseAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_to_exercise, parent, false);
        ExerciseViewHolder viewHolder = new ExerciseViewHolder(view);
        return viewHolder;
    }

    public void setLinearLayout(){

    }

    @Override
    public void onBindViewHolder(final ToExerciseAdapter.ExerciseViewHolder holder, int position) {
        Log.d("Binding in To: ", mExercises.get(position).getName());
        holder.bindExercise(mExercises.get(position));
        mViewHolder = holder;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView){
//        Log.v("Detach", "!!");
    }

    @Override
    public void onViewDetachedFromWindow(ExerciseViewHolder holder){
//        Log.v("Detach from window", "!!");
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }


    public void resetExercises(){
        mExercises.clear();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.ExerciseName) TextView mExerciseName;
        @BindView(R.id.At) TextView mAt;
        @BindView(R.id.X) TextView mX;
        @BindView(R.id.Ex) TextView mEx;
        @BindView(R.id.Sets) EditText mSets;
        @BindView(R.id.Reps) EditText mReps;
        @BindView(R.id.Weight) EditText mWeight;
        @BindView(R.id.Time) EditText mTime;
        @BindView(R.id.Distance) EditText mDistance;
        @BindView(R.id.Drag) ImageView mDrag;
        @BindView(R.id.Split) ImageView mSplit;

        private Context mContext;
        private View mItemView;

        private TextWatcher mSetWatcher;
        private TextWatcher mRepWatcher;
        private TextWatcher mWeightWatcher;
        private TextWatcher mTimeWatcher;
        private TextWatcher mDistanceWatcher;

        private MyCustomEditTextListener setsCustomEditTextListener;

        private Exercise mExercise;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            mItemView = itemView;
        }

        private void disableWeight(){
            mWeight.setVisibility(View.INVISIBLE);
            mAt.setVisibility(View.INVISIBLE);
        }

        private void disableTimeandDistance(){
            mEx.setVisibility(View.INVISIBLE);
            mTime.setVisibility(View.INVISIBLE);
            mDistance.setVisibility(View.INVISIBLE);
        }

        private void disableDistance(){
            mEx.setVisibility(View.INVISIBLE);
            mDistance.setVisibility(View.INVISIBLE);
        }

        private void disableRepsAndSets(){
            mReps.setVisibility(View.INVISIBLE);
            mSets.setVisibility(View.INVISIBLE);
            mX.setVisibility(View.INVISIBLE);
        }

        private void enableViews(){
            mReps.setVisibility(View.VISIBLE);
            mSets.setVisibility(View.VISIBLE);
            mX.setVisibility(View.VISIBLE);
            mEx.setVisibility(View.VISIBLE);
            mTime.setVisibility(View.VISIBLE);
            mDistance.setVisibility(View.VISIBLE);
            mWeight.setVisibility(View.VISIBLE);
            mAt.setVisibility(View.VISIBLE);
        }

        public void onClick(View v){
            int catcher = mExercise.getSets();
            if(v == mSplit){
                int currentPosition = mExercises.indexOf(mExercise);
                for(int i = 1; i <= catcher; i++){
                    Log.v("Loop", "#" + i);
                    Exercise exercise = mExercise;
                    exercise.setSets(1);
                    mExercises.add(currentPosition, exercise);
                    Log.v("Current: " + currentPosition, " vs actual: " + mExercises.indexOf(exercise));
                    mAdapter.notifyItemInserted(currentPosition);
                }
                mExercises.remove(currentPosition + catcher);
                mAdapter.notifyItemRemoved(currentPosition + catcher);
            }
        }

        public void bindExercise(final Exercise exercise) {
            String type = exercise.getType();

            if(type.equals(Constants.TYPE_BODYWEIGHT)){
                enableViews();
                disableWeight();
                disableTimeandDistance();
            }else if(type.equals(Constants.TYPE_WEIGHT)){
                enableViews();
                disableTimeandDistance();
            }else if(type.equals(Constants.TYPE_AEROBIC)){
                enableViews();
                disableWeight();
                disableRepsAndSets();
            }else if(type.equals(Constants.TYPE_TIME)){
                enableViews();
                disableRepsAndSets();
                disableWeight();
                disableDistance();
            }

            mExercise = exercise;
            mExerciseName.setText(exercise.getName());

            final int thisCatch = this.getLayoutPosition();

            Log.v("Setting values for " + mExercise.getName(), " @Layout " + this.getLayoutPosition() + "&Adapter " + this.getAdapterPosition());

            if(exercise.getSets() != null && exercise.getSets() != 0){
                Log.v("Sets were not null", "setting to " + exercise.getSets());
                this.mSets.setText(String.valueOf(exercise.getSets()));
            }else{
                Log.v("Sets were null", "setting to blank");
                this.mSets.setText("");
            }

            if(mExercise.getReps() != null && mExercise.getReps() != 0){
                Log.v("Reps were not null", "setting to " + mExercise.getSets());
                mReps.setText(String.valueOf(mExercise.getReps()));
            }else{
                Log.v("Reps were null", "setting to blank");
                mReps.setText("");
            }

            if(mExercise.getWeight() != null && mExercise.getWeight() != 0){
                Log.v("Weight was not null", "setting to " + mExercise.getSets());
                mWeight.setText(String.valueOf(mExercise.getWeight()));
            }else{
                Log.v("Weight was null", "setting to blank");
                mWeight.setText("");
            }

            if(mExercise.getTime() != null){
                if(!mExercise.getTime().equals("0:00")){
                    Log.v("Time was not null", "setting to " + mExercise.getTime());
                    mTime.setText(mExercise.getTime());
                }
            }else{
                Log.v("Time was null", "setting to blank");
                mTime.setText("");
            }

            if(mExercise.getDistance() != null){
                if( mExercise.getDistance() != 0){
                    Log.v("Distance was not null", "setting to " + mExercise.getSets());
                    mDistance.setText(String.valueOf(mExercise.getDistance()));
                }
            }else{
                Log.v("Distance was null", "setting to blank");
                mDistance.setText("");
            }

            Log.v("--------------------","--");

            if(exercise.getSets() != null){
                if(exercise.getSets() > 1){
                    mSplit.setVisibility(View.VISIBLE);
                }else{
                    mSplit.setVisibility(View.INVISIBLE);
                }
            }else{
                mSplit.setVisibility(View.INVISIBLE);
            }
            mSplit.setOnClickListener(this);

            if(exercise.getSets() != null){
                mSets.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "1000")});


                mSets.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Log.v("SetsWatcher", "Triggered");

                        String catcher = mSets.getText().toString();
                        Log.v("---", "---");
                        Log.v("!!!", mExercise.toString());
                        for(Exercise exerciseItem:mExercises){
                            Log.v("Sets", String.valueOf(exerciseItem.getSets()));
                        }
                        Log.v("---", "---");
                        Log.v("vPosition", String.valueOf(mExercises.indexOf(exercise)));
                        Log.v("AdapterPosition", String.valueOf(thisCatch));
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){

                            if(Integer.parseInt(catcher) > 1){
                                mSplit.setVisibility(View.VISIBLE);
                            }else{
                                mSplit.setVisibility(View.INVISIBLE);
                            }



//                            dtInterface.updateExercise(getAdapterPosition(), "sets", Integer.parseInt(catcher));
                            Log.v("Index of exercise:", String.valueOf(mExercises.indexOf(exercise)));
                            mExercises.get(getAdapterPosition()).setSets(Integer.parseInt(catcher));
                            Log.v("mExercises Size", String.valueOf(mExercises.size()));
                            Log.v("***", "***");
                            for(Exercise exerciseItem:mExercises){
                                Log.v("Sets", String.valueOf(exerciseItem.getSets()));
                            }
                            Log.v("***", "***");
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setSets(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                if(exercise.getSets() != 0){
                    mSets.setText(String.valueOf(exercise.getSets()));
                }
            }

            if(exercise.getReps() != null){
                mReps.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "1000")});

                mRepWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mReps.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setReps(Integer.parseInt(catcher));
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setReps(0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mReps.addTextChangedListener(mRepWatcher);

                if(exercise.getReps() != 0){
                    mReps.setText(String.valueOf(exercise.getReps()));
                }
            }

            if(exercise.getWeight() != null){
                mWeight.setFilters(new InputFilter[]{ new DoubleFilterMinMax("1", "1000")});

                mWeightWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mWeight.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            Log.v("Before:", catcher);
                            Double formattedWeight = Double.parseDouble(String.format("%.2f", Double.parseDouble(catcher)));
                            Log.v("After:", String.valueOf(formattedWeight));
                            mExercises.get(mExercises.indexOf(exercise)).setWeight(formattedWeight);
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setWeight(0.0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mWeight.addTextChangedListener(mWeightWatcher);

                if(exercise.getWeight() != 0){
                    mWeight.setText(String.valueOf(exercise.getWeight()));
                }
            }

            if(exercise.getTime() != null){
                mTimeWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mTime.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setTime(catcher);
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setTime("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mTime.addTextChangedListener(mTimeWatcher);

                if(exercise.getTime().length() != 0 && !(mExercises.get(mExercises.indexOf(exercise)).getTime().equals("0:00"))){
                    mTime.setText(String.valueOf(exercise.getTime()));
                }
            }

            if(exercise.getDistance() != null){
                mDistance.setFilters(new InputFilter[]{ new DoubleFilterMinMax("1", "1000")});

                mDistanceWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String catcher = mDistance.getText().toString();
                        if(!catcher.equals("") && mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setDistance(Double.parseDouble(catcher));
                        }else if(mExercises.indexOf(exercise) != -1){
                            mExercises.get(mExercises.indexOf(exercise)).setDistance(0.0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                };

                mDistance.addTextChangedListener(mDistanceWatcher);

                if(exercise.getDistance() != 0){
                    mDistance.setText(String.valueOf(exercise.getDistance()));
                }
            }

        }
        private class MyCustomEditTextListener implements TextWatcher {
            private int position;

            public void updatePosition(int position) {
                this.position = position;
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // no op
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mExercise.setSets(3);

                Log.v("Custom working", "!!");
                Log.v("Output", charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // no op
            }
        }
    }
}