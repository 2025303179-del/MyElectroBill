package com.example.myelectrobill;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricitybill.db";
    private static final int DATABASE_VERSION = 3;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
                "create table bill(" +
                        "no integer primary key autoincrement," +
                        "month text not null," +
                        "year integer not null," +
                        "unit integer not null," +
                        "rebate integer not null," +
                        "totalCharge real not null," +
                        "finalCost real not null);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bill");
        onCreate(db);
    }
}


