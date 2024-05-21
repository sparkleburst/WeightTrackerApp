package com.example.weighttrackingapp2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.weighttrackingapp2.databinding.ActivityGoalBinding;

public class GoalActivity extends AppCompatActivity {



    ActivityGoalBinding binding;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(GoalActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(GoalActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        databaseHelper = new DatabaseHelper(this);

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String goal = binding.weightGoal.getText().toString();

                if (goal.equals(""))
                    Toast.makeText(GoalActivity.this, "Please enter a goal", Toast.LENGTH_SHORT).show();
                else {
                    int goalNumber = Integer.parseInt(goal);
                    Boolean insert = databaseHelper.insertGoalData(goalNumber);

                    if (insert == true) {
                        Toast.makeText(GoalActivity.this, "Weight Goal Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), WeightActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(GoalActivity.this, "Failed to add Weight Goal. \nPlease Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

       // Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // intent.putExtra("data","Some value to be passed");

       // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
       //         0, intent, PendingIntent.FLAG_MUTABLE);
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

}