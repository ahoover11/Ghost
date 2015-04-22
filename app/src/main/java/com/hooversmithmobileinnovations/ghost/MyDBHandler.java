package com.hooversmithmobileinnovations.ghost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 4/13/2015.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Dictionary.db";
    public static final String TABLE_ENGLISH = "english";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORDS = "words";
    private Context myContext; //<-- declare a Context reference

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory,DATABASE_VERSION);
        myContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_ENGLISH + "(" + COLUMN_ID +" INTEGER PRIMARY KEY,"+ COLUMN_WORDS
                +" TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        try { //try catch for context

            BufferedReader reader = new BufferedReader(new InputStreamReader(myContext.getAssets().open("enable1.txt")));
            String currentLine = reader.readLine();

            while (currentLine != null) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_WORDS, currentLine);
                db.insert(TABLE_ENGLISH, null, values);
                currentLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            // ERROR
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ENGLISH);
        onCreate(db);
    }


    public boolean checkWord(String word)
    {
        String query = "SELECT * FROM " + TABLE_ENGLISH + " WHERE "+ COLUMN_WORDS + " = \"" + word + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);


        if(cursor.moveToFirst()){
            cursor.close();
            db.close();
            return  true;
        }
        else
        {
            db.close();
            return false;
        }

    }
    public List<String> getSuggestions(String partialWord)
    {
        String query = "SELECT * FROM " + TABLE_ENGLISH + " WHERE "+ COLUMN_WORDS + " Like '" + partialWord+"%'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);
        List<String> list = new ArrayList<String>();

        while (cursor.moveToNext())
        {
            list.add(cursor.getString(1));
        }

        cursor.close();

        return list;

    }

}

