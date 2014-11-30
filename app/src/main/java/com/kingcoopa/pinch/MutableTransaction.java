package com.kingcoopa.pinch;

import java.util.Date;

/**
 * Created by Aaron on 11/30/2014.
 */
public class MutableTransaction {
    private long _id;
    public double _amount = 0.0;
    public Date _date;
    public String _label = "";

    public MutableTransaction() {
        _date = new Date();
        _id = 0;
    }

    public MutableTransaction(Transaction transaction) {
        _amount = transaction.amount();
        _date = (Date)transaction.date().clone();
        _id = transaction.id();
        _label = transaction.label();
    }

    public MutableTransaction(double amount, Date date, String label) {
        _amount = amount;
        _date = date;
        _id = 0;
        _label = label;
    }

    public double amount() {
        return _amount;
    }

    public void setAmount(double val) {
        _amount = val;
    }

    public Date date() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    public long id() {
        return _id;
    }

    public void setId(long val) {
        _id = val;
    }

    public String label() { return _label; }

    public void setLabel(String label) {
        _label = label;
    }


}
