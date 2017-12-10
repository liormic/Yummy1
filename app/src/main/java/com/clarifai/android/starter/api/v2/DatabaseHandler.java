package com.clarifai.android.starter.api.v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Lior on 9/1/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "imageUrls";

    // Contacts table name
    private static final String TABLE_URLS = "urls";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "url_value";
    private static final String KEY_DATE = "url_date";




    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_URLS_TABLE = "CREATE TABLE " + TABLE_URLS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DATE+" TEXT"+")";
        db.execSQL(CREATE_URLS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URLS);

        // Create tables again
        onCreate(db);
    }


    public void addUrl(String uri){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, uri); // Uri value
     //   values.put(KEY_DATE, date); // Uri value

        // Inserting Row
        db.insert(TABLE_URLS, null, values);
        db.close(); // Closing database connection

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    public ArrayList<HistoryDataSource> retreiveUrls(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_URLS, new String[] { KEY_ID,
                        KEY_NAME, KEY_DATE },null, null, null, null, null);

        ArrayList<HistoryDataSource> dataSources = new ArrayList<HistoryDataSource>();
        if (cursor.moveToFirst()) {
            do{
              HistoryDataSource dataSource = new HistoryDataSource(getStringFromColumn(cursor,KEY_NAME),getStringFromColumn(cursor,KEY_DATE));

              dataSources.add(dataSource);
        }while (cursor.moveToNext());


            cursor.close();

        }


        return dataSources;

    }

    private String getStringFromColumn(Cursor cursor,String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return  cursor.getString(columnIndex);

    }



}