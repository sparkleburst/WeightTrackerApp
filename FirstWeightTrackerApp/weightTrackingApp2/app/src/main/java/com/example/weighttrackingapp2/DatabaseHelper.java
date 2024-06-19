package com.example.weighttrackingapp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

        MyDatabase.execSQL("create Table if not exists UserWeightGoal(email TEXT, goal INTEGER," +
                " FOREIGN KEY (email) REFERENCES AllUsers (email))");

        MyDatabase.execSQL("create Table if not exists UserWeightInfo(email TEXT, date TEXT, weight INTEGER, " +
                " FOREIGN KEY (email) REFERENCES AllUsers (email))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {

    }

    // create method to insert data
    public Boolean insertUserData(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = MyDatabase.insert("AllUsers", null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
        // return result != -1;
    }

    public Boolean insertGoalData(Integer goal) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", LoginActivity.loggedInEmail);
        contentValues.put("goal", goal);
        long result = MyDatabase.insert("UserWeightGoal", null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
        // return result != -1;
    }

    public Boolean insertWeightData(Integer weight) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", LoginActivity.loggedInEmail);
        contentValues.put("date", currentDate);
        contentValues.put("weight", weight);
        long result = MyDatabase.insert("UserWeightInfo", null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
        // return result != -1;
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

}
