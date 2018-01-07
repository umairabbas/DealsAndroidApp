package com.dealspok.dealspok.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Umi on 25.09.2017.
 */

public class DealDatabaseHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "deals.db";
    private static final String TABLE_NAME = "tbdeals";
    public static final String DEAL_COLUMN_ID = "_id";
    public static final String DEAL_COLUMN_NAME = "name";
    DealsHelper openHelper;
    private SQLiteDatabase database;

    public DealDatabaseHelper(Context context){
        openHelper = new DealsHelper(context);
        database = openHelper.getWritableDatabase();
    }
    public void saveDealRecord(String id, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEAL_COLUMN_ID, id);
        contentValues.put(DEAL_COLUMN_NAME, name);
        database.insert(TABLE_NAME, null, contentValues);
    }
    public Cursor getTimeRecordList() {
        return database.rawQuery("select * from " + TABLE_NAME, null);
    }
    private class DealsHelper extends SQLiteOpenHelper {

        public DealsHelper(Context context) {
            // TODO Auto-generated constructor stub
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                    + DEAL_COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + DEAL_COLUMN_NAME + " TEXT )" );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS"+ TABLE_NAME);
            onCreate(db);
        }

    }
}