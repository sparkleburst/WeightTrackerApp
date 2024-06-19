package com.example.weighttrackingapp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "Weighttracker.db";
    Calendar calendar = Calendar.getInstance();
    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Weighttracker.db", null, 1);
    }

    // create a database with three tables in it
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("create Table if not exists AllUsers(email TEXT PRIMARY KEY, password TEXT)");

        MyDatabase.execSQL("create Table if not exists UserWeightGoal(email TEXT PRIMARY KEY, goal INTEGER," +
                " FOREIGN KEY (email) REFERENCES AllUsers (email))");

        MyDatabase.execSQL("create Table if not exists UserWeightInfo(email TEXT, date TEXT, weight INTEGER, " +
                " FOREIGN KEY (email) REFERENCES AllUsers (email))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {

    }

    // create method to insert data
    public boolean insertData(String tableName, ContentValues contentValues) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        long result = MyDatabase.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public Boolean insertGoalData(Integer goal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", LoginActivity.loggedInEmail);
        contentValues.put("goal", goal);
        Log.d("DatabaseHelper", "Inserting Goal: " + goal + " for email: " + LoginActivity.loggedInEmail);
        return insertData("UserWeightGoal", contentValues);
    }

    public Boolean insertWeightData(Integer weight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", LoginActivity.loggedInEmail);
        contentValues.put("date", currentDate);
        contentValues.put("weight", weight);
        Log.d("DatabaseHelper", "Inserting Weight: " + weight + " for email: " + LoginActivity.loggedInEmail + " on date: " + currentDate);
        return insertData("UserWeightInfo", contentValues);
    }


    public Boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from AllUsers where email = ?",
                new String[]{email});

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
        // return cursor.getCount() > 0;
    }

    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from AllUsers where email = ?" +
                " and password = ?", new String[]{email, password});

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkUserGoal(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM UserWeightGoal WHERE email = ?", new String[]{email});
        boolean goalExists = cursor.getCount() > 0;
        cursor.close();
        return goalExists;
    }

    public Cursor getUserWeight(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightInfo WHERE email = ? ORDER BY date DESC LIMIT 1", new String[]{email});
    }

    public Cursor getUserGoal(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightGoal WHERE email = ?", new String[]{email});
    }

    public Cursor getAllUserWeights(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        return MyDatabase.rawQuery("SELECT * FROM UserWeightInfo WHERE email = ? ORDER BY date DESC", new String[]{email});
    }
}
