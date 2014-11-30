package com.kingcoopa.pinch;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by acooper on 11/6/2014.
 */
public class TransactionHistoryGroupView extends FrameLayout {
    private View _child;
    private TextView _monthTextView;

    static SimpleDateFormat sDateFormatter = new SimpleDateFormat("MMM d");

    public TransactionHistoryGroupView(Context context) {
        super(context);

        _child = FrameLayout.inflate(context, R.layout.history_group_view, this);
        _monthTextView = (TextView)_child.findViewById(R.id.historyGroup_monthTextView);
    }

    public void setDate(Date date) {
        _monthTextView.setText(sDateFormatter.format(date));
    }
}
