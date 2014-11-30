package com.kingcoopa.pinch;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Aaron on 11/6/2014.
 */
public class HistoryExpandableAdapter extends BaseExpandableListAdapter {
    public ArrayList<Date> _months = new ArrayList<Date>();
    public ArrayList<ArrayList<Transaction>> _transactions = new ArrayList<ArrayList<Transaction>>();

    public void setTransactions(ArrayList<Transaction> transactions) {
        _months.clear();
        _transactions.clear();

        SortedMap<Date, ArrayList<Transaction>> transactionDict = new TreeMap<Date, ArrayList<Transaction>>();

        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            cal.setTime(transactions.get(i).date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date date = cal.getTime();

            ArrayList<Transaction> monthTransactions = transactionDict.get(date);

            if(monthTransactions == null) {
                monthTransactions = new ArrayList<Transaction>();
                transactionDict.put(date, monthTransactions);
            }

            monthTransactions.add(transaction);
        }

        for (Date date : transactionDict.keySet()) {
            _months.add(date);
            _transactions.add(transactionDict.get(date));
        }

        Collections.reverse(_months);
        Collections.reverse(_transactions);

        notifyDataSetInvalidated();
    }

    @Override
    public int getGroupCount() {
        return _months.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _transactions.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _months.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _transactions.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = new TransactionHistoryGroupView(parent.getContext());
        }

        ((TransactionHistoryGroupView)convertView).setDate(_months.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Transaction transaction = _transactions.get(groupPosition).get(childPosition);

        if(convertView == null) {
            convertView = new TransactionHistoryItemView(parent.getContext(), transaction);
        } else {
            ((TransactionHistoryItemView)convertView).updateWithTransaction(transaction);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
