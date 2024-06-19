package com.example.weighttrackingapp2.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.weighttrackingapp2.model.DatabaseHelper;

public class LoginViewModel extends ViewModel {

    private DatabaseHelper databaseHelper;

    public LoginViewModel(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public boolean loginUser(String email, String password) {
        // Logic to validate user credentials
        return databaseHelper.checkEmailPassword(email, password);
    }

    public boolean checkUserGoalExistence(String email) {
        // Logic to check if a goal exists for the user
        return databaseHelper.checkUserGoal(email);
    }
}

