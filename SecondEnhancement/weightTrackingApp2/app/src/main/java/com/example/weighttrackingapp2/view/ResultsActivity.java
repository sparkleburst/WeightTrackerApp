package com.example.weighttrackingapp2.view;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.weighttrackingapp2.R;
import com.example.weighttrackingapp2.model.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
                populateWeightHistory(email, weightGoal);
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

    private void populateWeightHistory(String email, int weightGoal) {
        Cursor weightHistoryCursor = null;
        try {
            weightHistoryCursor = databaseHelper.getAllUserWeights(email);
            if (weightHistoryCursor != null) {
                // Add table header
                TableRow headerRow = new TableRow(this);
                // Set background with border
                headerRow.setBackground(ContextCompat.getDrawable(this, R.drawable.table_row_border));

                // Date column
                TextView dateTitleTextView = new TextView(this);
                dateTitleTextView.setText("Date");
                dateTitleTextView.setPadding(8, 8, 8, 8);
                dateTitleTextView.setGravity(Gravity.CENTER);

                // Time column
                TextView timeTitleTextView = new TextView(this);
                timeTitleTextView.setText("Time");
                timeTitleTextView.setPadding(8, 8, 8, 8);
                timeTitleTextView.setGravity(Gravity.CENTER);

                // Weight column
                TextView weightTitleTextView = new TextView(this);
                weightTitleTextView.setText("Weight (lbs)");
                weightTitleTextView.setPadding(8, 8, 8, 8);
                weightTitleTextView.setGravity(Gravity.CENTER);

                // Distance to Goal column
                TextView distanceTitleTextView = new TextView(this);
                distanceTitleTextView.setText("Distance to Goal");
                distanceTitleTextView.setPadding(8, 8, 8, 8);
                distanceTitleTextView.setGravity(Gravity.CENTER);

                headerRow.addView(dateTitleTextView);
                headerRow.addView(timeTitleTextView);
                headerRow.addView(weightTitleTextView);
                headerRow.addView(distanceTitleTextView);

                weightHistoryTable.addView(headerRow);

                // Add table data
                while (weightHistoryCursor.moveToNext()) {
                    try {
                        // Create a new TableRow for each record
                        TableRow tableRow = new TableRow(this);

                        // Set background with border
                        tableRow.setBackground(ContextCompat.getDrawable(this, R.drawable.table_row_border));

                        String dateStr = weightHistoryCursor.getString(weightHistoryCursor.getColumnIndexOrThrow("date"));
                        int weightLbs = weightHistoryCursor.getInt(weightHistoryCursor.getColumnIndexOrThrow("weight"));

                        // Format date
                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        originalFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set input time zone to UTC
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        SimpleDateFormat targetTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        Date date = originalFormat.parse(dateStr);
                        String formattedDate = targetDateFormat.format(date);
                        String formattedTime = targetTimeFormat.format(date);

// Set the time zone for output formats to the device's default time zone
                        targetDateFormat.setTimeZone(TimeZone.getDefault());
                        targetTimeFormat.setTimeZone(TimeZone.getDefault());

// Format date and time in the user's local time zone
                        formattedDate = targetDateFormat.format(date);
                        formattedTime = targetTimeFormat.format(date);

                        // Create TextViews for each piece of data
                        TextView dateTextView = new TextView(this);
                        dateTextView.setText(formattedDate);
                        dateTextView.setPadding(8, 8, 8, 8);
                        dateTextView.setGravity(Gravity.CENTER);

                        TextView timeTextView = new TextView(this);
                        timeTextView.setText(formattedTime);
                        timeTextView.setPadding(8, 8, 8, 8);
                        timeTextView.setGravity(Gravity.CENTER);

                        TextView weightTextView = new TextView(this);
                        weightTextView.setText(String.valueOf(weightLbs));
                        weightTextView.setPadding(8, 8, 8, 8);
                        weightTextView.setGravity(Gravity.CENTER);

                        int distanceToGoal = weightGoal - weightLbs;
                        TextView distanceToGoalTextView = new TextView(this);
                        distanceToGoalTextView.setText(String.valueOf(distanceToGoal));
                        distanceToGoalTextView.setPadding(8, 8, 8, 8);
                        distanceToGoalTextView.setGravity(Gravity.CENTER);

                        // Add TextViews to the TableRow
                        tableRow.addView(dateTextView);
                        tableRow.addView(timeTextView);
                        tableRow.addView(weightTextView);
                        tableRow.addView(distanceToGoalTextView);

                        // Add the populated TableRow to the TableLayout
                        weightHistoryTable.addView(tableRow);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle the exception (e.g., log it or show an error message)
                    }
                }
            }
        } finally {
            if (weightHistoryCursor != null) {
                weightHistoryCursor.close();
            }
        }
    }

}
