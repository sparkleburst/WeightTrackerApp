package com.example.weighttrackingapp2.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighttrackingapp2.model.DatabaseHelper;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final DatabaseHelper databaseHelper;

    public LoginViewModelFactory(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(databaseHelper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}