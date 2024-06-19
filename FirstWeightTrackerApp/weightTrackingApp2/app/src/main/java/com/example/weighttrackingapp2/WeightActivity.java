package com.example.weighttrackingapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weighttrackingapp2.databinding.ActivityWeightBinding;

import java.text.DateFormat;
import java.util.Calendar;


public class WeightActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    ActivityWeightBinding binding;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.todaysDate.setText(currentDate);

        binding.weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = binding.dailyWeight.getText().toString();

                if (weight.equals(""))
                    Toast.makeText(WeightActivity.this, "Please enter today's weight", Toast.LENGTH_SHORT).show();
                else {
                    int weightNumber = Integer.parseInt(weight);
                    Boolean insert = databaseHelper.insertWeightData(weightNumber);

                    if (insert == true) {
                        Toast.makeText(WeightActivity.this, "Daily Weight Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(WeightActivity.this, "Failed to add Daily Weight. Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // TODO: user enters weight, clicks button to add to database, is directed to results table

        // TODO: make redirect so user can change get back to login screen
        // TODO: "Not your account? login"

        // TODO: make redirect so user can change goal weight if they want


    }
}