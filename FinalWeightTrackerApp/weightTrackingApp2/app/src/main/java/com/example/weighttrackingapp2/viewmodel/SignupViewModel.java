package com.example.weighttrackingapp2.viewmodel;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Patterns;

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
    public int signUpUser(String email, String password, String confirmPassword) {
        // Validate user input
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return 0; // All fields are mandatory
        }

        if (!isValidEmail(email)) {
            return 1; // Invalid email
        }

        if (!isValidPassword(password)) {
            return 2; // Invalid password
        }

        if (!password.equals(confirmPassword)) {
            return 3; // Passwords don't match
        }

        // Check if the user already exists
        if (databaseHelper.checkEmail(email)) {
            return 4; // User already exists
        }

        // Insert the new user data into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        if (databaseHelper.insertData("AllUsers", contentValues)) {
            return 5; // Signup successful
        } else {
            return 6; // Database insert error
        }
    }

    // Validate email format
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate password format
    private boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and contain at least one letter, one number, and one special character
        return password != null && password.length() >= 8 && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$");
    }
}