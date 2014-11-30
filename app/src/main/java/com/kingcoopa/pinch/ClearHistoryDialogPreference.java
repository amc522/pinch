package com.kingcoopa.pinch;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by Aaron on 11/8/2014.
 */
public class ClearHistoryDialogPreference extends DialogPreference {
    public ClearHistoryDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult) {
            TransactionManager.getInstance().clearAllTransactions();
        }
    }

}
