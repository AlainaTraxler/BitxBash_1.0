package com.epicodus.bitxbit.models;

import android.util.Log;

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
    public String type;

    public Workout() {
    }

    public Workout(ArrayList<Exercise> exercises, String _name, String _type) {
        this.exercises = exercises;
        createdOn = DateFormat.getDateTimeInstance().format(new Date());
        type = _type;

        if(_name == null){
            name = createdOn;
        }else name = _name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
