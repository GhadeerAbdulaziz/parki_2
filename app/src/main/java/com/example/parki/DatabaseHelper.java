package com.example.parki;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="smartparking.db";
    public static final String TABLE_NAME="users";
    public static final String COL_1 ="ID";
    public static final String COL_2 ="Name";
    public static final String COL_3 ="email";
    public static final String COL_4 ="Password";
    public static final String COL_5 ="Phone";
    public static final String COL_6 ="Cartype";
    public static final String COL_7 ="Carnum";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT, Email TEXT, Password TEXT, Phone TEXT, Cartype TEXT, Carnum TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public void addUser (String name,String email,String password,String phone,String car,String carnum){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name",name);
        contentValues.put("Email",email);
        contentValues.put("Password",password);
        contentValues.put("Phone",phone);
        contentValues.put("CarType",car);
        contentValues.put("CarNum",carnum);
        db.insert(TABLE_NAME,null,contentValues);
        db.close();



    }
    public boolean checkUser (String email,String password){
        String [] columns ={ COL_1 };
        SQLiteDatabase db =getReadableDatabase();
        String selection = COL_3 + "=?" + " and " + COL_4 + "=?";
        String []selectionArgs ={email,password};
        Cursor cursor=db.query(TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        int count=cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0)
            return true;
        else
          return false;
     }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COL_1
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COL_3 + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_NAME, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }



}
