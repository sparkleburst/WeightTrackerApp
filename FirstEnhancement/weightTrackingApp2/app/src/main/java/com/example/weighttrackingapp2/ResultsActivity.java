package com.example.weighttrackingapp2;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    TextView currentWeightTextView;
    TextView weightGoalTextView;
    TextView progressTextView;
    TableLayout weightHistoryTable;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize views
        currentWeightTextView = findViewById(R.id.current_weight);
        weightGoalTextView = findViewById(R.id.weight_goal);
        progressTextView = findViewById(R.id.progress);
        weightHistoryTable = findViewById(R.id.weight_history_table);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve data
        String email = LoginActivity.loggedInEmail;
        Cursor weightCursor = null;
        Cursor goalCursor = null;

        try {
            weightCursor = databaseHelper.getUserWeight(email);
            goalCursor = databaseHelper.getUserGoal(email);

            // Display data
            if (weightCursor != null && weightCursor.moveToFirst() && goalCursor != null && goalCursor.moveToFirst()) {
                int currentWeight = weightCursor.getInt(weightCursor.getColumnIndexOrThrow("weight"));
                int weightGoal = goalCursor.getInt(goalCursor.getColumnIndexOrThrow("goal"));

                Log.d("ResultsActivity", "Retrieved Current Weight: " + currentWeight + " lbs");
                Log.d("ResultsActivity", "Retrieved Weight Goal: " + weightGoal + " lbs");

                currentWeightTextView.setText("Current Weight: " + currentWeight + " lbs");
                weightGoalTextView.setText("Weight Goal: " + weightGoal + " lbs");

                int progressInLbs = weightGoal - currentWeight;
                progressTextView.setText("Progress: " + progressInLbs + " lbs");

                // Populate weight history
                populateWeightHistory(email);
            } else {
                Log.d("ResultsActivity", "No data found.");
                currentWeightTextView.setText("No weight data available.");
                weightGoalTextView.setText("No goal data available.");
                progressTextView.setText("Progress: N/A");
            }
        } finally {
            if (weightCursor != null) {
                weightCursor.close();
            }
            if (goalCursor != null) {
                goalCursor.close();
            }
        }
    }

    private void populateWeightHistory(String email) {
        Cursor weightHistoryCursor = null;
        try {
            weightHistoryCursor = databaseHelper.getAllUserWeights(email);
            while (weightHistoryCursor != null && weightHistoryCursor.moveToNext()) {
                String date = weightHistoryCursor.getString(weightHistoryCursor.getColumnIndexOrThrow("date"));
                int weightLbs = weightHistoryCursor.getInt(weightHistoryCursor.getColumnIndexOrThrow("weight"));

                TableRow tableRow = new TableRow(this);
                TextView dateTextView = new TextView(this);
                dateTextView.setText(date);
                dateTextView.setPadding(8, 8, 8, 8);

                TextView weightTextView = new TextView(this);
                weightTextView.setText(String.valueOf(weightLbs));
                weightTextView.setPadding(8, 8, 8, 8);

                tableRow.addView(dateTextView);
                tableRow.addView(weightTextView);

                weightHistoryTable.addView(tableRow);
            }
        } finally {
            if (weightHistoryCursor != null) {
                weightHistoryCursor.close();
            }
        }
    }
}
