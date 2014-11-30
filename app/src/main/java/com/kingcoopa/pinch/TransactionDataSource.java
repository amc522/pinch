package com.kingcoopa.pinch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Aaron on 11/6/2014.
 */
public class TransactionDataSource {
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LABEL = "label";
    public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_AMOUNT, COLUMN_DATE, COLUMN_LABEL };

    private SQLiteDatabase _database;
    private PinchSQLiteHelper _dbHelper;

    public TransactionDataSource(Context context) {
        _dbHelper = new PinchSQLiteHelper(context);
    }

    public void open() throws SQLException {
        _database = _dbHelper.getWritableDatabase();
    }

    public void close() {
        _dbHelper.close();
    }

    public Transaction createTransaction(double amount, Date date) {
        return createTransaction(amount, date);
    }

    public Transaction createTransaction(double amount, Date date, String label) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date.getTime());
        values.put(COLUMN_LABEL, label);

        long insertId = _database.insert(TABLE_TRANSACTIONS, null, values);

        return new Transaction(amount, date, label, insertId);
    }

    public boolean updateTransaction(MutableTransaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, transaction.amount());
        values.put(COLUMN_DATE, transaction.date().getTime());
        values.put(COLUMN_LABEL, transaction.label());

        return _database.update(TABLE_TRANSACTIONS, values, COLUMN_ID + " = " + transaction.id(), null) > 0;
    }

    public void deleteTransaction(Transaction transaction) {
        _database.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = " + transaction.id(), null);
    }

    public void deleteAllTransactions() {
        _database.delete(TABLE_TRANSACTIONS, "", null);
    }

    public void deleteTransactionsBeforeDate(Date date) {
        _database.delete(TABLE_TRANSACTIONS, COLUMN_DATE + " < " + date.getTime(), null);
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = _database.query(TABLE_TRANSACTIONS, ALL_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            Transaction transaction = cursorToTransaction(cursor);
            transactions.add(transaction);
            cursor.moveToNext();
        }

        cursor.close();
        return transactions;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int labelIndex = cursor.getColumnIndex(COLUMN_LABEL);

        Transaction transaction = new Transaction(cursor.getDouble(amountIndex), new Date(cursor.getLong(dateIndex)), cursor.getString(labelIndex), cursor.getLong(idIndex));
        return transaction;
    }
}
