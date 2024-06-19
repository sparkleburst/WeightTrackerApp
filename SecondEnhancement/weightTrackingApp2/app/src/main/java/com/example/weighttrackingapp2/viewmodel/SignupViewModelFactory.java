package com.example.weighttrackingapp2.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.weighttrackingapp2.model.DatabaseHelper;

public class SignupViewModelFactory implements ViewModelProvider.Factory {
    private final DatabaseHelper databaseHelper;

    public SignupViewModelFactory(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignupViewModel.class)) {
            return (T) new SignupViewModel(databaseHelper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
