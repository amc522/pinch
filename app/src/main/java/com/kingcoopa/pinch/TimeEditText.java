package com.kingcoopa.pinch;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aaron on 11/30/2014.
 */
public class TimeEditText extends EditText implements TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
    private TimePickerDialog _timePickerDialog;
    private Date _time;

    public TimeEditText(Context context) {
        super(context);
        init();
    }

    public TimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setInputType(EditorInfo.TYPE_NULL);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if(!isInEditMode()) {
            _timePickerDialog = new TimePickerDialog(getContext(), this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(getContext()));
            _timePickerDialog.setOnCancelListener(this);
            _timePickerDialog.setOnDismissListener(this);
        }

        setTime(cal.getTime());
    }

    public Date getTime() {
        return _time;
    }

    public void setTime(Date time) {
        _time = time;

        SimpleDateFormat format;

        if(DateFormat.is24HourFormat(getContext())) {
            format = new SimpleDateFormat("H:mm");
        } else {
            format = new SimpleDateFormat("h:mm a");
        }

        setText(format.format(_time));
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if(focused) {
            _timePickerDialog.show();
            InputMethodManager keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        setTime(cal.getTime());
        clearFocus();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        clearFocus();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        clearFocus();
    }
}
