package com.example.weighttrackingapp2.view;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.weighttrackingapp2.R;
import com.example.weighttrackingapp2.model.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ResultsActivity extends AppCompatActivity {

    private static final int ENTRIES_PER_PAGE = 7; // Define pagination variables
    private int currentPage = 0; // Define current page variable

    TextView currentWeightTextView;
    TextView weightGoalTextView;
    TextView progressTextView;
    LinearLayout tablesContainer; // New LinearLayout to hold the table
    Button previousPageButton;
    Button nextPageButton;
    DatabaseHelper databaseHelper;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize views
        currentWeightTextView = findViewById(R.id.current_weight);
        weightGoalTextView = findViewById(R.id.weight_goal);
        progressTextView = findViewById(R.id.progress);
        tablesContainer = findViewById(R.id.tables_container);
        previousPageButton = findViewById(R.id.previous_page_button);
        nextPageButton = findViewById(R.id.next_page_button);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the logged-in user's email
        email = LoginActivity.loggedInEmail;

        // Retrieve and display data
        retrieveAndDisplayData();

        // Center the text by setting the layout gravity of tablesContainer
        tablesContainer.setGravity(Gravity.CENTER);

        // Set up button listeners
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    populateWeightHistory();
                }
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                populateWeightHistory();
            }
        });

        // Redirect to GoalActivity if the user wants to change their goal weight
        TextView changeGoalText = findViewById(R.id.change_goal_text);
        changeGoalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoalActivity.class);
                startActivity(intent);
            }
        });

        // Redirect to WeightActivity if the user wants to change their weight
        TextView changeWeightText = findViewById(R.id.change_weight_text);
        changeWeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WeightActivity.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveAndDisplayData() {
        Cursor weightCursor = null;
        Cursor goalCursor = null;

        try {
            weightCursor = databaseHelper.getUserWeight(email);
            goalCursor = databaseHelper.getUserGoal(email);

            if (weightCursor != null && weightCursor.moveToFirst() && goalCursor != null && goalCursor.moveToFirst()) {
                int currentWeight = weightCursor.getInt(weightCursor.getColumnIndexOrThrow("weight"));
                int weightGoal = goalCursor.getInt(goalCursor.getColumnIndexOrThrow("goal"));

                currentWeightTextView.setText("Current Weight: " + currentWeight + " lbs");
                weightGoalTextView.setText("Weight Goal: " + weightGoal + " lbs");

                if (currentWeight == weightGoal) {
                    // Show "Congratulations! You've reached your goal!" message
                    progressTextView.setText("Congratulations!\nYou've reached your goal!");
                    // Call makeNotification() to send the notification
                    makeNotification();
                } else {
                    int progressInLbs = weightGoal - currentWeight;
                    progressTextView.setText("Distance to Goal: " + progressInLbs + " lbs");
                }

                populateWeightHistory();
            } else {
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

    public void makeNotification(){
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(),channelID);
        builder.setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("You did it!")
                .setContentText("You reached your weight goal.\nYou are amazing!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID,
                        "Some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());

    }

    private void populateWeightHistory() {
        Cursor weightHistoryCursor = null;
        try {
            weightHistoryCursor = databaseHelper.getAllUserWeights(email);
            if (weightHistoryCursor != null) {
                int totalEntries = weightHistoryCursor.getCount();
                int totalPages = (int) Math.ceil((double) totalEntries / ENTRIES_PER_PAGE);

                previousPageButton.setEnabled(currentPage > 0);
                nextPageButton.setEnabled(currentPage < totalPages - 1);

                tablesContainer.removeAllViews();
                TableLayout table = createNewTable();
                addTableHeader(table);

                int startIndex = currentPage * ENTRIES_PER_PAGE;
                if (weightHistoryCursor.moveToPosition(startIndex)) {
                    for (int i = 0; i < ENTRIES_PER_PAGE && !weightHistoryCursor.isAfterLast(); i++) {
                        try {
                            TableRow tableRow = new TableRow(this);
                            tableRow.setBackground(ContextCompat.getDrawable(this, R.drawable.table_row_border));

                            String dateStr = weightHistoryCursor.getString(weightHistoryCursor.getColumnIndexOrThrow("date"));
                            int weightLbs = weightHistoryCursor.getInt(weightHistoryCursor.getColumnIndexOrThrow("weight"));
                            int goalAtGivenDate = weightHistoryCursor.getInt(weightHistoryCursor.getColumnIndexOrThrow("goal_at_time"));

                            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            SimpleDateFormat targetDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                            SimpleDateFormat targetTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                            Date date = originalFormat.parse(dateStr);
                            targetDateFormat.setTimeZone(TimeZone.getDefault());
                            targetTimeFormat.setTimeZone(TimeZone.getDefault());

                            String formattedDate = targetDateFormat.format(date);
                            String formattedTime = targetTimeFormat.format(date);

                            TextView dateTextView = new TextView(this);
                            dateTextView.setText(formattedDate);
                            dateTextView.setPadding(8, 8, 4, 8);
                            dateTextView.setGravity(Gravity.CENTER);

                            TextView timeTextView = new TextView(this);
                            timeTextView.setText(formattedTime);
                            timeTextView.setPadding(4, 8, 8, 8);
                            timeTextView.setGravity(Gravity.CENTER);

                            TextView weightTextView = new TextView(this);
                            weightTextView.setText(String.valueOf(weightLbs));
                            weightTextView.setPadding(12, 8, 8, 8);
                            weightTextView.setGravity(Gravity.CENTER);

                            TextView goalAtGivenDateTextView = new TextView(this);
                            goalAtGivenDateTextView.setText(String.valueOf(goalAtGivenDate));
                            goalAtGivenDateTextView.setPadding(8, 8, 16, 8);
                            goalAtGivenDateTextView.setGravity(Gravity.CENTER);

                            Button deleteButton = new Button(this);
                            deleteButton.setText("Delete");
                            deleteButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FA8072")));
                            deleteButton.setPadding(8, 8, 8, 8);
                            deleteButton.setGravity(Gravity.CENTER);

                            deleteButton.setOnClickListener(v -> {
                                boolean deleted = databaseHelper.deleteWeightEntryByDate(email, dateStr);
                                if (deleted) {
                                    Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                                    currentPage = 0;
                                    populateWeightHistory();
                                } else {
                                    Toast.makeText(this, "Failed to delete entry", Toast.LENGTH_SHORT).show();
                                }
                            });

                            tableRow.addView(dateTextView);
                            tableRow.addView(timeTextView);
                            tableRow.addView(weightTextView);
                            tableRow.addView(goalAtGivenDateTextView);
                            tableRow.addView(deleteButton);

                            table.addView(tableRow);
                            weightHistoryCursor.moveToNext();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                tablesContainer.addView(table);
            }
        } finally {
            if (weightHistoryCursor != null) {
                weightHistoryCursor.close();
            }
        }
    }

    private TableLayout createNewTable() {
        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);
        return table;
    }

    private void addTableHeader(TableLayout table) {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackground(ContextCompat.getDrawable(this, R.drawable.table_row_border));

        TextView dateTitleTextView = new TextView(this);
        dateTitleTextView.setText("Date");
        dateTitleTextView.setPadding(8, 8, 4, 8);
        dateTitleTextView.setGravity(Gravity.CENTER);

        TextView timeTitleTextView = new TextView(this);
        timeTitleTextView.setText("Time");
        timeTitleTextView.setPadding(4, 8, 8, 8);
        timeTitleTextView.setGravity(Gravity.CENTER);

        TextView weightTitleTextView = new TextView(this);
        weightTitleTextView.setText("Weight");
        weightTitleTextView.setPadding(12, 8, 8, 8);
        weightTitleTextView.setGravity(Gravity.CENTER);

        TextView goalAtGivenDateTitleTextView = new TextView(this);
        goalAtGivenDateTitleTextView.setText("Goal");
        goalAtGivenDateTitleTextView.setPadding(8, 8, 16, 8);
        goalAtGivenDateTitleTextView.setGravity(Gravity.CENTER);

        TextView actionsTitleTextView = new TextView(this);
        actionsTitleTextView.setText("Delete");
        actionsTitleTextView.setPadding(8, 8, 8, 8);
        actionsTitleTextView.setGravity(Gravity.CENTER);

        headerRow.addView(dateTitleTextView);
        headerRow.addView(timeTitleTextView);
        headerRow.addView(weightTitleTextView);
        headerRow.addView(goalAtGivenDateTitleTextView);
        headerRow.addView(actionsTitleTextView);

        table.addView(headerRow);
    }


}
