package com.epicodus.bitxbit.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.models.Exercise;
import com.epicodus.bitxbit.ui.MainActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 3/7/2017.
 */

public class FromExerciseAdapter extends RecyclerView.Adapter<FromExerciseAdapter.ExerciseViewHolder>{
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private Context mContext;

    //DataTransferInterface dtInterface;

    public FromExerciseAdapter(Context context, ArrayList<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }

    @Override
    public FromExerciseAdapter.ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_from_exercise, parent, false);
        ExerciseViewHolder viewHolder = new ExerciseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FromExerciseAdapter.ExerciseViewHolder holder, int position) {
        holder.bindExercise(mExercises.get(position));
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView_Name) TextView mTextView_ExerciseName;

        public ExerciseViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindExercise(Exercise exercise) {
            mTextView_ExerciseName.setText(exercise.getName());
        }

        public Exercise getExercise(){
            return mExercises.get(getAdapterPosition());
        }

        public String getType(){
            return "exercise";
        }
    }
}
