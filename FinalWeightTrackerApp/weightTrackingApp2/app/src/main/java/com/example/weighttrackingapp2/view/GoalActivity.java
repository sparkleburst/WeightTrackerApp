package com.example.weighttrackingapp2.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighttrackingapp2.databinding.ActivityGoalBinding;
import com.example.weighttrackingapp2.model.DatabaseHelper;
import com.example.weighttrackingapp2.viewmodel.GoalViewModel;

public class GoalActivity extends AppCompatActivity {

    ActivityGoalBinding binding;
    DatabaseHelper databaseHelper;
    GoalViewModel goalViewModel;

    private static final int PERMISSION_REQUEST_NOTIFICATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GoalActivity", "onCreate called");
        binding = ActivityGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(GoalActivity.this,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(GoalActivity.this,
                        new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        PERMISSION_REQUEST_NOTIFICATION);
            }
        }

        databaseHelper = new DatabaseHelper(this);
        goalViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new GoalViewModel(databaseHelper);
            }
        }).get(GoalViewModel.class);

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goal = binding.weightGoal.getText().toString();
                if (goal.equals("")) {
                    Toast.makeText(GoalActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                } else {
                    int goalNumber = Integer.parseInt(goal);
                    boolean insert = databaseHelper.insertGoalData(LoginActivity.loggedInEmail, goalNumber);

                    if (insert) {
                        Toast.makeText(GoalActivity.this, "Current Goal Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), WeightActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(GoalActivity.this, "Failed to add Weight Goal. \nPlease Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Redirect to LoginActivity if the user wants to switch accounts
        binding.switchAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

// Redirect to WeightActivity if the user wants to view their weight data
        binding.changeCurrentWeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WeightActivity.class);
                startActivity(intent);
            }
        });
    }

}