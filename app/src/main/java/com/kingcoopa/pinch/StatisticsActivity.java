package com.kingcoopa.pinch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;


public class StatisticsActivity extends Activity {
    private ProgressBar _monthlyProgressBar;
    private TextView _actualExpensesText;
    private TextView _expectedExpensesText;
    private TextView _dailyAvgText;
    private TextView _transAvgText;
    private TextView _remainingAllowanceText;
    private TextView _adjustedAllowanceText;
    private TextView _projectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        _monthlyProgressBar = (ProgressBar)findViewById(R.id.stats_monthlyProgress);
        _actualExpensesText = (TextView)findViewById(R.id.stats_actualText);
        _expectedExpensesText = (TextView)findViewById(R.id.stats_expectedText);
        _dailyAvgText = (TextView)findViewById(R.id.stats_dailyAvgText);
        _transAvgText = (TextView)findViewById(R.id.stats_transAvgText);
        _remainingAllowanceText = (TextView)findViewById(R.id.stats_remainingText);
        _adjustedAllowanceText = (TextView)findViewById(R.id.stats_adjAllowanceText);
        _projectedText = (TextView)findViewById(R.id.stats_projectedText);

        fillStats();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillStats() {
        TransactionManager transManager = TransactionManager.getInstance();

        double expected = transManager.getExpectedExpensesToDate();
        double actual = transManager.getActualExpensesToDate();
        double dailyAvg = transManager.getAvgDailyExpensesToDate();
        double allowance = transManager.getDailyAllowance();
        double transAvg = transManager.getTransactionAvgForCurrentMonth();
        double remaining = transManager.getRemainingBalance();
        double adjAllowance = transManager.getAdjustedAllowance();
        double monthlyAllowance = transManager.getMonthlyAllowance();
        double projected = transManager.getProjectedMonthlyExpense();

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

        _actualExpensesText.setText(currencyFormatter.format(actual));
        if(actual > expected) {
            _actualExpensesText.setTextColor(getResources().getColor(R.color.allowance_negative));
        } else {
            _actualExpensesText.setTextColor(getResources().getColor(R.color.allowance_good));
        }

        _expectedExpensesText.setText(currencyFormatter.format(expected));

        _dailyAvgText.setText(currencyFormatter.format(dailyAvg));
        if(dailyAvg > allowance) {
            _dailyAvgText.setTextColor(getResources().getColor(R.color.allowance_negative));
        } else {
            _dailyAvgText.setTextColor(getResources().getColor(R.color.allowance_good));
        }

        _transAvgText.setText(currencyFormatter.format(transAvg));
        _remainingAllowanceText.setText(currencyFormatter.format(remaining));
        _adjustedAllowanceText.setText(currencyFormatter.format(adjAllowance));

        _projectedText.setText(currencyFormatter.format(projected));
        if(projected > monthlyAllowance) {
            _projectedText.setTextColor(getResources().getColor(R.color.allowance_negative));
        } else {
            _projectedText.setTextColor(getResources().getColor(R.color.allowance_good));
        }

        _monthlyProgressBar.setMax((int)monthlyAllowance);
        _monthlyProgressBar.setProgress((int)actual);
        _monthlyProgressBar.setSecondaryProgress((int)expected);
    }
}
