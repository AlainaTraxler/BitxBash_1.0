package com.epicodus.bitxbit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.models.Exercise;
import com.epicodus.bitxbit.models.Workout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 3/7/2017.
 */

public class FromWorkoutAdapter extends RecyclerView.Adapter<FromWorkoutAdapter.WorkoutViewHolder>{
    private ArrayList<Workout> mWorkouts = new ArrayList<>();
    private Context mContext;

    public FromWorkoutAdapter(Context context, ArrayList<Workout> exercises) {
        mContext = context;
        mWorkouts = exercises;
    }

    @Override
    public FromWorkoutAdapter.WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_from_exercise, parent, false);
        WorkoutViewHolder viewHolder = new WorkoutViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FromWorkoutAdapter.WorkoutViewHolder holder, int position) {
        holder.bindWorkout(mWorkouts.get(position));
    }

    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView_Name)
        TextView mTextView_WorkoutName;
        private Context mContext;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindWorkout(Workout workout) {
            mTextView_WorkoutName.setText(workout.getName());
        }

        public Workout getWorkout(){
            return mWorkouts.get(getAdapterPosition());
        }
    }
}
