package com.kingcoopa.pinch;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aaron on 11/29/2014.
 */
public class DateEditText extends EditText implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener {
    private DatePickerDialog _datePickerDialog;
    private Date _date;
    private DateFormat _dateFormat;

    public DateEditText(Context context) {
        super(context);
        init();
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setInputType(EditorInfo.TYPE_NULL);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day, 0, 0, 0);
        _date = cal.getTime();

        if(!isInEditMode()) {
            _datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);
            _datePickerDialog.setOnCancelListener(this);
        }

        _dateFormat = DateFormat.getDateInstance();

        setText(_dateFormat.format(_date));
    }

    public DatePicker getDatePicker() {
        return _datePickerDialog.getDatePicker();
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
        setText(_dateFormat.format(_date));
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if(focused) {
            _datePickerDialog.show();
            InputMethodManager keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);

        setDate(cal.getTime());

        clearFocus();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        clearFocus();
    }
}
