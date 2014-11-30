package com.kingcoopa.pinch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class DailyAllowanceActivity extends Activity implements TextWatcher, TextView.OnEditorActionListener {

    private LinearLayout _addTransactionLayout;
    private TextView _allowanceTextView;
    private EditText _transactionEditText;
    private Button _addTransactionButton;
    private Button _acceptTransactionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_transactions, true);

        TransactionManager.getInstance().init(this);

        setContentView(R.layout.activity_daily_allowance);
        _addTransactionLayout = (LinearLayout)findViewById(R.id.addTransactionLayout);
        _addTransactionLayout.setVisibility(View.INVISIBLE);

        _allowanceTextView = (TextView)findViewById(R.id.allowanceTextView);
        updateAllowanceView();

        TextView currencySymbol = (TextView)findViewById(R.id.main_currencySymbol);
        currencySymbol.setText(Currency.getInstance(Locale.getDefault()).getSymbol());

        _transactionEditText = (EditText)findViewById(R.id.transactionEditText);
        _transactionEditText.setFilters(new InputFilter[]{new com.kingcoopa.pinch.CurrencyFormatInputFilter()});
        _transactionEditText.setHint(NumberFormat.getCurrencyInstance().format(0.0).substring(1));

        _transactionEditText.addTextChangedListener(this);
        _transactionEditText.setOnEditorActionListener(this);
        _transactionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    keyboard.showSoftInput(v, 0);
                } else if(keyboard.isAcceptingText()) {
                    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        _acceptTransactionButton = (Button)findViewById(R.id.acceptTransactionButton);
        _addTransactionButton = (Button)findViewById(R.id.addTransactionButton);
        _addTransactionButton.setText(Currency.getInstance(Locale.getDefault()).getSymbol());
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAllowanceView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_allowance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddTransactionButtonClicked(View view) {
        if(_addTransactionLayout.getVisibility() == View.VISIBLE) {
            closeTransactionAdder();
        } else {
            openTransactionAdder();
        }
    }

    public void onAcceptTransactionClicked(View view) {
        _addTransactionLayout.setVisibility(View.INVISIBLE);
        TransactionManager.getInstance().addTransaction(Double.parseDouble(_transactionEditText.getText().toString()), new Date());
        updateAllowanceView();
    }

    public void onCloseTransactionButtonClicked(View view) {
        closeTransactionAdder();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(_transactionEditText.getText() == null || _transactionEditText.getText().length() == 0 || _transactionEditText.getText().toString().contentEquals(".")) {
            _acceptTransactionButton.setEnabled(false);
        } else if(Double.parseDouble(_transactionEditText.getText().toString()) == 0.0) {
            _acceptTransactionButton.setEnabled(false);
        } else {
            _acceptTransactionButton.setEnabled(true);
        }
    }

    private void openTransactionAdder() {
        _addTransactionLayout.setVisibility(View.VISIBLE);
        _transactionEditText.clearComposingText();
        _transactionEditText.setText("");
        _transactionEditText.setHint("0.00");
        _transactionEditText.setSelected(true);
        _acceptTransactionButton.setEnabled(false);
    }

    private void closeTransactionAdder() {
        _addTransactionLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(v != _transactionEditText) {
            return true;
        }

        if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        {
            closeTransactionAdder();
        }

        return true;
    }

    private void updateAllowanceView() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        double allowance = TransactionManager.getInstance().getAllowanceToDate();
        double dailyAllowance = TransactionManager.getInstance().getDailyAllowance();

        if(allowance >= dailyAllowance) {
            _allowanceTextView.setTextColor(getResources().getColor(R.color.allowance_good));
        } else if(allowance > 0) {
            _allowanceTextView.setTextColor(getResources().getColor(R.color.allowance_low));
        } else {
            _allowanceTextView.setTextColor(getResources().getColor(R.color.allowance_negative));
        }

        _allowanceTextView.setText(formatter.format(TransactionManager.getInstance().getAllowanceToDate()));
    }
}
