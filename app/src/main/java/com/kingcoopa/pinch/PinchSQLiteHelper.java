package com.kingcoopa.pinch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aaron on 11/6/2014.
 */
public class PinchSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pinch.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "create table " +
            TransactionDataSource.TABLE_TRANSACTIONS + "(" +
            TransactionDataSource.COLUMN_ID + " integer primary key autoincrement, " +
            TransactionDataSource.COLUMN_AMOUNT + " real, " +
            TransactionDataSource.COLUMN_DATE + " integer, " +
            TransactionDataSource.COLUMN_LABEL + " text" + ");";

    public PinchSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TransactionDataSource.TABLE_TRANSACTIONS + ";");
        onCreate(db);
    }
}
