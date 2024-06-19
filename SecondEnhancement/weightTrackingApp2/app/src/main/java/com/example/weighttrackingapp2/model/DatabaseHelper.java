package com.example.weighttrackingapp2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Name of the database
    public static final String databaseName = "Weighttracker.db";

    // Constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 1);
    }

    // Called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        // Create tables if they don't already exist
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS AllUsers(email TEXT PRIMARY KEY, password TEXT)");
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS UserWeightGoal(email TEXT PRIMARY KEY, goal INTEGER, FOREIGN KEY (email) REFERENCES AllUsers (email))");
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS UserWeightInfo(email TEXT, date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, weight INTEGER, FOREIGN KEY (email) REFERENCES AllUsers (email))");
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {
        // This method is currently empty because no upgrade logic is implemented
    }

    // Insert data into the specified table
    public boolean insertData(String tableName, ContentValues contentValues) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        long result = MyDatabase.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    // Insert goal data for the logged-in user
    public Boolean insertGoalData(String email, Integer goal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("goal", goal);
        Log.d("DatabaseHelper", "Inserting Goal: " + goal + " for email: " + email);
        return insertData("UserWeightGoal", contentValues);
    }

    public Boolean insertWeightData(String email, Integer weight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("weight", weight);
        // The timestamp is automatically added by SQLite using the DEFAULT CURRENT_TIMESTAMP constraint
        Log.d("DatabaseHelper", "Inserting Weight: " + weight + " for email: " + email);
        return insertData("UserWeightInfo", contentValues);
    }

    // Check if an email exists in the AllUsers table
    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from AllUsers where email = ?", new String[]{email});
        boolean emailExists = cursor.getCount() > 0;
        cursor.close();
        return emailExists;
    }

    // Check if an email and password combination exists in the AllUsers table
    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from AllUsers where email = ? and password = ?", new String[]{email, password});
        boolean credentialsValid = cursor.getCount() > 0;
        cursor.close();
        return credentialsValid;
    }

    // Check if a goal exists for a specific user
    public Boolean checkUserGoal(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM UserWeightGoal WHERE email = ?", new String[]{email});
        boolean goalExists = cursor.getCount() > 0;
        cursor.close();
        return goalExists;
    }

    // Get the latest weight entry for a specific user
    public Cursor getUserWeight(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightInfo WHERE email = ? ORDER BY date DESC LIMIT 1", new String[]{email});
    }

    // Get the weight goal for a specific user
    public Cursor getUserGoal(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightGoal WHERE email = ?", new String[]{email});
    }

    // Get all weight entries for a specific user
    public Cursor getAllUserWeights(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightInfo WHERE email = ? ORDER BY date DESC", new String[]{email});
    }

    public int getLatestWeight(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int latestWeight = 0;

        // Query to get the latest weight entry for the given user
        String query = "SELECT weight FROM UserWeightInfo WHERE email = ? ORDER BY date DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        // Check if the cursor has data
        if (cursor.moveToFirst()) {
            latestWeight = cursor.getInt(cursor.getColumnIndex("weight"));
        }

        // Close the cursor and database connection
        cursor.close();
        db.close();

        return latestWeight;
    }
}
