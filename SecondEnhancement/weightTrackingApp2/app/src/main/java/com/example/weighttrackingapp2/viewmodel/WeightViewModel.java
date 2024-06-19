package com.example.weighttrackingapp2.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeightViewModel extends ViewModel {

    private MutableLiveData<String> currentWeight = new MutableLiveData<>();

    public LiveData<String> getCurrentWeight() {
        return currentWeight;
    }

    // Method to update current weight
    public void updateCurrentWeight(String weight) {
        currentWeight.setValue(weight);
    }

    // Add methods to update data here

}
