package com.kingcoopa.pinch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HistoryActivity extends Activity implements TransactionManager.TransactionEventListener, ExpandableListView.OnItemLongClickListener, EditTransactionDialog.EditTransactionDialogListener {
    private enum EditTransactionMode {
        None,
        Edit,
        Add
    }

    ExpandableListView _historyListView;
    HistoryExpandableAdapter _historyExpandableAdapter;
    TextView _titleTextView;
    SimpleDateFormat _titleFormatter = new SimpleDateFormat("MMM yyyy");
    EditTransactionMode _editMode = EditTransactionMode.None;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        _historyListView = (ExpandableListView)findViewById(R.id.history_listView);
        _historyExpandableAdapter = new HistoryExpandableAdapter();

        _historyListView.setOnItemLongClickListener(this);
        _historyListView.setAdapter(_historyExpandableAdapter);

        Calendar cal = Calendar.getInstance();
        _historyExpandableAdapter.setTransactions(TransactionManager.getInstance().getTransactions());

        _titleTextView = (TextView)findViewById(R.id.history_titleLabel);
        _titleTextView.setText(_titleFormatter.format(new Date()));
        TransactionManager.getInstance().addTransactionListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
        } else if(id == R.id.action_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddTransactionButtonClicked(View view) {
        EditTransactionDialog dialog = new EditTransactionDialog();
        dialog.show(getFragmentManager(), "AddTransaction");
        _editMode = EditTransactionMode.Add;
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        _historyExpandableAdapter.setTransactions(TransactionManager.getInstance().getTransactions());
    }

    public void onTransactionUpdated(Transaction transaction) {
        _historyExpandableAdapter.setTransactions(TransactionManager.getInstance().getTransactions());
    }

    public void onTransactionDeleted() {
        _historyExpandableAdapter.setTransactions(TransactionManager.getInstance().getTransactions());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
            int childPosition = ExpandableListView.getPackedPositionChild(id);

            final Transaction transaction = (Transaction)_historyExpandableAdapter.getChild(groupPosition, childPosition);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Modify Transaction?");
            builder.setCancelable(true);
            builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onEditTransactionButtonClicked(transaction);
                }
            });

            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TransactionManager.getInstance().deleteTransaction(transaction);
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create().show();

            return true;
        }
        return false;
    }

    private void onEditTransactionButtonClicked(Transaction transaction) {
        _editMode = EditTransactionMode.Edit;

        EditTransactionDialog dialog = new EditTransactionDialog();
        Bundle args = new Bundle();
        args.putDouble("amount", transaction.amount());
        args.putLong("date", transaction.date().getTime());
        args.putString("label", transaction.label());
        args.putLong("id", transaction.id());
        dialog.setArguments(args);

        dialog.show(getFragmentManager(), "EditTransaction");
    }

    @Override
    public void OnDialogPositiveClick(DialogFragment dialog) {
        EditTransactionDialog editTransactionDialog = (EditTransactionDialog) dialog;

        if(_editMode == EditTransactionMode.Add) {
            TransactionManager.getInstance().addTransaction(editTransactionDialog.transaction());
        } else if(_editMode == EditTransactionMode.Edit) {
            TransactionManager.getInstance().updateTransaction(editTransactionDialog.transaction());
        }

        _editMode = EditTransactionMode.None;
    }
}
