package com.epicodus.bitxbit.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.epicodus.bitxbit.R;
import com.epicodus.bitxbit.models.Exercise;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alaina Traxler on 3/6/2017.
 */

public class FirebaseFromExerciseViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.textView_Name) TextView mTextViewName;

    private View mView;

    public FirebaseFromExerciseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mView = itemView;
    }

    public void bindExercise(Exercise exercise) {
        mTextViewName.setText(exercise.getName());
    }
}
