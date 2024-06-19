package com.example.weighttrackingapp2.viewmodel;

import android.content.ContentValues;

import androidx.lifecycle.ViewModel;
import com.example.weighttrackingapp2.model.DatabaseHelper;

public class SignupViewModel extends ViewModel {
    private DatabaseHelper databaseHelper;

    // Constructor to initialize the DatabaseHelper
    public SignupViewModel(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // Method to initialize the ViewModel with DatabaseHelper instance
    public void init(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // Method to handle user signup
    public boolean signUpUser(String email, String password, String confirmPassword) {
        // Validate user input
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false; // All fields are mandatory
        }

        if (!password.equals(confirmPassword)) {
            return false; // Passwords don't match
        }

        // Check if the user already exists
        if (databaseHelper.checkEmail(email)) {
            return false; // User already exists
        }

        // Insert the new user data into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        return databaseHelper.insertData("AllUsers", contentValues);
    }
}