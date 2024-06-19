package com.example.weighttrackingapp2.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.weighttrackingapp2.model.DatabaseHelper;

public class GoalViewModel extends ViewModel {

    private DatabaseHelper databaseHelper;

    public GoalViewModel(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public boolean insertGoal(String userEmail, int goalWeight) {
        return databaseHelper.insertGoalData(userEmail, goalWeight);
    }

    public int getLatestWeight(String userEmail) {
        return databaseHelper.getLatestWeight(userEmail);
    }
}
