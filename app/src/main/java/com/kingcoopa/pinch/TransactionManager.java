package com.kingcoopa.pinch;

import android.content.Context;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Aaron on 11/6/2014.
 */
public class TransactionManager {
    public interface TransactionEventListener {
        public void onTransactionAdded(Transaction transaction);
        public void onTransactionUpdated(Transaction transaction);
        public void onTransactionDeleted();
    }

    private static TransactionManager sInstance = new TransactionManager();

    private ArrayList<WeakReference<TransactionEventListener>> _listeners = new ArrayList<WeakReference<TransactionEventListener>>();

    private ArrayList<Transaction> _transactions;
    TransactionDataSource _dataSource;
    Context _context;

    public static TransactionManager getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        _context = context;
        _dataSource = new TransactionDataSource(_context);
        try {
            _dataSource.open();
        } catch (SQLException ex) {

        }

        boolean deleteOld = PreferenceManager.getDefaultSharedPreferences(_context).getBoolean("pref_key_transactions_delete_old", true);

        if(deleteOld) {
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
            _dataSource.deleteTransactionsBeforeDate(cal.getTime());
        }

        _transactions = _dataSource.getAllTransactions();
    }

    public void addTransactionListener(TransactionEventListener listener) {
        if(listener == null) {
            return;
        }

        _listeners.add(new WeakReference<TransactionEventListener>(listener));
    }

    public void removeTransactionListener(TransactionEventListener listener) {
        if(listener == null) {
            return;
        }

        _listeners.remove(listener);
    }

    public Transaction addTransaction(MutableTransaction transaction) {
        return addTransaction(transaction.amount(), transaction.date(), transaction.label());
    }

    public Transaction addTransaction(double amount, Date date) {
        return addTransaction(amount, date, "");
    }

    public Transaction addTransaction(double amount, Date date, String label) {
        Transaction transaction = _dataSource.createTransaction(amount, date, label);
        _transactions.add(transaction);

        Collections.sort(_transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction lhs, Transaction rhs) {
                return lhs.date().compareTo(rhs.date());
            }
        });

        removeExpiredListeners();

        for(WeakReference<TransactionEventListener> listener : _listeners) {
            listener.get().onTransactionAdded(transaction);
        }

        return transaction;
    }

    public boolean updateTransaction(MutableTransaction mutTransaction) {
        int index = -1;

        for (int i = 0; i < _transactions.size(); i++) {
            if(mutTransaction.id() == _transactions.get(i).id()) {
                index = i;
                break;
            }
        }

        if(index < 0) {
            return false;
        }

        if(!_dataSource.updateTransaction(mutTransaction)) {
            return false;
        }

        Transaction updatedTransaction = new Transaction(mutTransaction.amount(), mutTransaction.date(), mutTransaction.label(), mutTransaction.id());
        _transactions.set(index, updatedTransaction);

        removeExpiredListeners();

        for(WeakReference<TransactionEventListener> listener : _listeners) {
            listener.get().onTransactionUpdated(updatedTransaction);
        }

        return true;
    }

    public void deleteTransaction(Transaction transaction) {
        _transactions.remove(transaction);
        _dataSource.deleteTransaction(transaction);

        removeExpiredListeners();

        for(WeakReference<TransactionEventListener> listener : _listeners) {
            listener.get().onTransactionDeleted();
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return _transactions;
    }

    public ArrayList<Transaction> getTransactionsForMonth(int year, int month) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Calendar cal = Calendar.getInstance();

        for(Transaction transaction : _transactions) {
            cal.setTime(transaction.date());

            if(cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month) {
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public ArrayList<Transaction> getTransactionsForCurrentMonth() {
        Calendar currCal = Calendar.getInstance();
        return getTransactionsForMonth(currCal.get(Calendar.YEAR), currCal.get(Calendar.MONTH));
    }

    public double getAllowanceToDate() {
        double actualExpensesToDate = getActualExpensesToDate();
        double expectedExpensesToDate = getExpectedExpensesToDate();

        return expectedExpensesToDate - actualExpensesToDate;
    }

    public double getDailyAllowance() {
        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return getMonthlyAllowance() / daysInMonth;
    }

    public double getMonthlyAllowance() {
        String prefStr = PreferenceManager.getDefaultSharedPreferences(_context).getString("pref_key_general_monthly_limit", "750.0");
        return Double.parseDouble(prefStr);
    }

    public double getAvgDailyExpensesToDate() {
        return getActualExpensesToDate() / Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public double getExpectedExpensesToDate() {
        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return getMonthlyAllowance() * ((double)day / (double)daysInMonth);
    }


    public double getActualExpensesToDate() {
        double expenses = 0.0;

        for(Transaction transaction : getTransactionsForCurrentMonth()) {
            expenses += transaction.amount();
        }

        return expenses;
    }

    public double getRemainingBalance() {
        return getMonthlyAllowance() - getActualExpensesToDate();
    }

    public double getAdjustedAllowance() {
        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return getRemainingBalance() / (double)(daysInMonth - day + 1);
    }

    public double getTransactionAvgForCurrentMonth() {
        double sum = 0.0f;
        ArrayList<Transaction> transactions = getTransactionsForCurrentMonth();
        for(Transaction transaction : transactions) {
            sum += transaction.amount();
        }

        return sum / (double)transactions.size();
    }

    public double getProjectedMonthlyExpense() {
        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return getAvgDailyExpensesToDate() * daysInMonth;
    }

    public void clearAllTransactions() {
        _transactions.clear();
        _dataSource.deleteAllTransactions();
    }

    private void removeExpiredListeners() {
        int i = _listeners.size() - 1;

        while(i >= 0) {
            if(_listeners.get(i).get() == null) {
                _listeners.remove(i);
            }

            --i;
        }
    }
}
