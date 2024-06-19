package com.example.weighttrackingapp2.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighttrackingapp2.databinding.ActivityWeightBinding;
import com.example.weighttrackingapp2.model.DatabaseHelper;
import com.example.weighttrackingapp2.viewmodel.WeightViewModel;

import java.text.DateFormat;
import java.util.Calendar;

public class WeightActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    ActivityWeightBinding binding;
    DatabaseHelper databaseHelper;

    WeightViewModel weightViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this); // Initialize the database helper

        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class); // Create instance of WeightViewModel

        binding.todaysDate.setText(currentDate);

        binding.weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = binding.dailyWeight.getText().toString();

                if (weight.isEmpty()) {
                    Toast.makeText(WeightActivity.this, "Please enter today's weight", Toast.LENGTH_SHORT).show();
                } else {
                    int weightNumber = Integer.parseInt(weight);
                    boolean insert = databaseHelper.insertWeightData(LoginActivity.loggedInEmail, weightNumber);

                    if (insert) {
                        Toast.makeText(WeightActivity.this, "Daily Weight Added Successfully", Toast.LENGTH_SHORT).show();
                        weightViewModel.updateCurrentWeight(weight); // Update the current weight
                        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(WeightActivity.this, "Failed to add Daily Weight. Please Try Again", Toast.LENGTH_SHORT).show();
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

        // Redirect to GoalActivity if the user wants to change their goal weight
        binding.changeGoalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoalActivity.class);
                startActivity(intent);
            }
        });

        // TODO: user enters weight, clicks button to add to database, is directed to results table (Already implemented)
        // TODO: make redirect so user can change get back to login screen (Implemented above)
        // TODO: "Not your account? login" (Implemented above)
        // TODO: make redirect so user can change goal weight if they want (Implemented above)
    }
}