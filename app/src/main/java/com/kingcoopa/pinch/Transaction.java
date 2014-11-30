package com.kingcoopa.pinch;

import java.util.Date;

/**
 * Created by Aaron on 11/6/2014.
 */
public class Transaction {
    private long _id;
    private double _amount = 0.0;
    private Date _date;
    private String _label = "";

    public Transaction(double amount, Date date, long id) {
        _amount = amount;
        _date = date;
        _id = id;
    }

    public Transaction(double amount, Date date, String label, long id) {
        _amount = amount;
        _date = date;
        _id = id;
        _label = label;
    }

    public double amount() {
        return _amount;
    }

    public Date date() {
        return _date;
    }

    public long id() {
        return _id;
    }

    public String label() { return _label; }
}