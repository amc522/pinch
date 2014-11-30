package com.kingcoopa.pinch;

/**
 * Created by Aaron on 11/8/2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class EditTransactionDialog extends DialogFragment {
    enum Mode {
        Edit,
        Add
    }

    public interface EditTransactionDialogListener {
        public void OnDialogPositiveClick(DialogFragment dialog);
    }

    private EditTransactionDialogListener _listener;
    private MutableTransaction _transaction;
    private Mode _mode;

    public EditTransactionDialog() {
        super();

        _transaction = new MutableTransaction();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        parseArgs();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_transaction_dialog, null);

        final EditText amountInput = (EditText)dialogView.findViewById(R.id.addTransactionDialog_amountInput);
        amountInput.setFilters(new InputFilter[] {new CurrencyFormatInputFilter()});
        amountInput.setHint(NumberFormat.getCurrencyInstance().format(0.0).substring(1));

        Calendar dateEditCal = Calendar.getInstance();

        final DateEditText dateInput = (DateEditText)dialogView.findViewById(R.id.addTransactionDialog_dateInput);
        dateInput.getDatePicker().setMaxDate(dateEditCal.getTimeInMillis());

        dateEditCal.set(Calendar.DAY_OF_MONTH, 1);
        dateInput.getDatePicker().setMinDate(dateEditCal.getTimeInMillis());
        dateInput.setDate(_transaction.date());

        final TimeEditText timeInput = (TimeEditText)dialogView.findViewById(R.id.addTransactionDialog_timeInput);
        timeInput.setTime(_transaction.date());

        final EditText labelInput = (EditText)dialogView.findViewById(R.id.addTransactionDialog_labelInput);
        labelInput.setText(_transaction.label());

        TextView currencySymbol = (TextView)dialogView.findViewById(R.id.addTransactionDialog_currencySymbol);
        currencySymbol.setText(Currency.getInstance(Locale.getDefault()).getSymbol());

        amountInput.setHint(NumberFormat.getCurrencyInstance().format(0.0).substring(1));

        if(_transaction.amount() > 0.0) {
            amountInput.setText(NumberFormat.getCurrencyInstance().format(_transaction.amount()).substring(1));
        }

        final DialogFragment dialogFragment = this;

        String titleStr;
        String positiveButtonStr;

        if(_mode == Mode.Edit) {
            positiveButtonStr = "Update";
            titleStr = "Edit Transaction";
        } else {
            positiveButtonStr = "Add";
            titleStr = "Add Transaction";
        }

        builder.setTitle(titleStr);
        builder.setView(dialogView).
                setPositiveButton(positiveButtonStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String amountStr = amountInput.getText().toString();

                                if (amountStr.isEmpty()) {
                                    return;
                                }

                                _transaction.setAmount(Double.parseDouble(amountStr));

                                if (_transaction.amount() == 0.0) {
                                    return;
                                }

                                Calendar cal = Calendar.getInstance();
                                Calendar tempCal = Calendar.getInstance();
                                tempCal.setTime(dateInput.getDate());
                                cal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                                tempCal.setTime(timeInput.getTime());
                                cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
                                cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

                                _transaction.setDate(cal.getTime());
                                _transaction.setLabel(labelInput.getText().toString());

                                if (_listener != null) {
                                    _listener.OnDialogPositiveClick(dialogFragment);
                                }
                            }
                        }
                ).
                setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            _listener = (EditTransactionDialogListener)activity;
        } catch (ClassCastException ex) {
            _listener = null;
        }
    }

    public MutableTransaction transaction() {
        return _transaction;
    }

    private void parseArgs() {
        Bundle args = getArguments();

        if(args == null || args.size() == 0) {
            _mode = Mode.Add;
            return;
        }

        _mode = Mode.Edit;
        _transaction.setAmount(args.getDouble("amount"));
        _transaction.setDate(new Date(args.getLong("date")));
        _transaction.setLabel(args.getString("label"));
        _transaction.setId(args.getLong("id"));
    }
}
