package com.example.weighttrackingapp2.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.weighttrackingapp2.util.EmailValidator;

import org.jetbrains.annotations.Nullable;

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
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS AllUsers(email TEXT PRIMARY KEY, password TEXT)");
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS UserWeightGoal(email TEXT PRIMARY KEY, goal INTEGER, FOREIGN KEY (email) REFERENCES AllUsers (email))");
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS UserWeightInfo(email TEXT, date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, weight INTEGER, goal_at_time INTEGER, FOREIGN KEY (email) REFERENCES AllUsers (email))");

        // Add indexing for performance optimization
        MyDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_user_email ON AllUsers(email)");
        MyDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_goal_email ON UserWeightGoal(email)");
        MyDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_weight_email ON UserWeightInfo(email)");
        MyDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_weight_date ON UserWeightInfo(date)");
        MyDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_weight_email_date ON UserWeightInfo(email, date)");
    }

    // Called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {
        // This method is currently empty because no upgrade logic is implemented
    }

    // Insert data into the specified table
    public boolean insertData(String tableName, ContentValues contentValues) {
        try (SQLiteDatabase MyDatabase = this.getWritableDatabase()) {
            long result = MyDatabase.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        }
    }

    // Insert goal data for the logged-in user
    public Boolean insertGoalData(String email, Integer goal) {
        if (!isValidEmail(email)) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("goal", goal);
        Log.d("DatabaseHelper", "Inserting Goal: " + goal + " for email: " + email);
        return insertData("UserWeightGoal", contentValues);
    }

    // Insert weight data for the logged-in user
    public Boolean insertWeightData(String email, Integer weight) {
        if (!isValidEmail(email)) {
            return false;
        }

        // Retrieve the current goal
        Cursor goalCursor = getUserGoal(email);
        int currentGoal = 0;
        if (goalCursor != null && goalCursor.moveToFirst()) {
            currentGoal = goalCursor.getInt(goalCursor.getColumnIndexOrThrow("goal"));
            goalCursor.close();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("weight", weight);
        contentValues.put("goal_at_time", currentGoal);  // Add the current goal

        Log.d("DatabaseHelper", "Inserting Weight: " + weight + " for email: " + email + " with Goal: " + currentGoal);
        return insertData("UserWeightInfo", contentValues);
    }

    // Check if an email exists in the AllUsers table
    public Boolean checkEmail(String email) {
        if (!isValidEmail(email)) {
            return false;
        }
        try (SQLiteDatabase MyDatabase = this.getReadableDatabase();
             Cursor cursor = MyDatabase.rawQuery("SELECT 1 FROM AllUsers WHERE email = ?", new String[]{email})) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    // Check if an email and password combination exists in the AllUsers table
    public Boolean checkEmailPassword(String email, String password) {
        if (!isValidEmail(email)) {
            return false;
        }
        try (SQLiteDatabase MyDatabase = this.getReadableDatabase();
             Cursor cursor = MyDatabase.rawQuery("SELECT 1 FROM AllUsers WHERE email = ? AND password = ?", new String[]{email, password})) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    // Check if a goal exists for a specific user
    public Boolean checkUserGoal(String email) {
        if (!isValidEmail(email)) {
            return false;
        }
        try (SQLiteDatabase MyDatabase = this.getReadableDatabase();
             Cursor cursor = MyDatabase.rawQuery("SELECT * FROM UserWeightGoal WHERE email = ?", new String[]{email})) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    // Get the latest weight entry for a specific user
    public Cursor getUserWeight(String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightInfo WHERE email = ? ORDER BY date DESC LIMIT 1", new String[]{email});
    }

    // Get the weight goal for a specific user
    public Cursor getUserGoal(String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightGoal WHERE email = ?", new String[]{email});
    }

    // Get all weight entries for a specific user
    public Cursor getAllUserWeights(String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightInfo WHERE email = ? ORDER BY date DESC", new String[]{email});
    }

    // Get the latest weight entry for a specific user
    @SuppressLint("Range")
    public int getLatestWeight(String email) {
        if (!isValidEmail(email)) {
            return 0;
        }
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT weight FROM UserWeightInfo WHERE email = ? ORDER BY date DESC LIMIT 1", new String[]{email})) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("weight"));
            }
            return 0;
        }
    }

    // Delete a weight entry from a specific date
    public boolean deleteWeightEntryByDate(String email, String dateStr) {
        if (!isValidEmail(email)) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("UserWeightInfo", "email = ? AND date = ?", new String[]{email, dateStr});
        db.close();
        return deletedRows > 0;
    }

    // Delete all weight entries for a specific user before a certain date
    public boolean deleteWeightEntriesBeforeDate(String email, long dateInMillis) {
        if (!isValidEmail(email)) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("UserWeightInfo", "email = ? AND date < ?", new String[]{email, String.valueOf(dateInMillis)});
        db.close();
        return deletedRows > 0;
    }

    // Validate email format
    private boolean isValidEmail(String email) {
        return email != null && EmailValidator.isValidEmail(email);
    }
}
