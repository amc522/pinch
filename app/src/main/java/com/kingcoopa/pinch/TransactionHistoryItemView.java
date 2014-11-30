package com.kingcoopa.pinch;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Aaron on 11/6/2014.
 */
public class TransactionHistoryItemView extends FrameLayout {
    Transaction _transaction;

    TextView _amountView;
    TextView _timeView;
    TextView _labelView;
    View _child;

    static SimpleDateFormat sDateFormatter = new SimpleDateFormat("h:mm a");

    public TransactionHistoryItemView(Context context, Transaction transaction) {
        super(context);

        _child = FrameLayout.inflate(context, R.layout.history_child_view, this);
        _amountView = (TextView)_child.findViewById(R.id.historyItem_amountTextView);
        _timeView = (TextView)_child.findViewById(R.id.historyItem_timeTextView);
        _labelView = (TextView)_child.findViewById(R.id.historyItem_labelTextView);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        //addView(_child, new LayoutParams(400, 30));
        updateWithTransaction(transaction);
    }

    public void updateWithTransaction(Transaction transaction) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        _amountView.setText(formatter.format(transaction.amount()));
        _timeView.setText(sDateFormatter.format(transaction.date()));

        if(!transaction.label().isEmpty()) {
            _labelView.setText("(" + transaction.label() + ")");
        } else {
            _labelView.setText("");
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
