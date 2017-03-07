package com.epicodus.bitxbit.models;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alaina Traxler on 3/7/2017.
 */

public class Workout {
    public String name;
    public ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    public String createdOn;
    public String pushId;

    public Workout() {
    }

    public Workout(ArrayList<Exercise> exercises, String name) {
        this.exercises = exercises;
        createdOn = DateFormat.getDateTimeInstance().format(new Date());
        if(name == null){
            name = createdOn;
        }
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
